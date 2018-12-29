package com.innov8.memeit.Activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomViews.DrawableBadge
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.Fragments.ProfileFragment
import com.innov8.memeit.Loaders.FavoriteMemeLoader
import com.innov8.memeit.Loaders.HomeMemeLoader
import com.innov8.memeit.Loaders.TrendingMemeLoader
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*

abstract class MainActivity : AppCompatActivity() {
    private val titles = arrayOf("MemeIt", "Trending", "Favorites")
    private var searchItem: MenuItem? = null
    private var notifItem: MenuItem? = null
    private val pagerAdapter by lazy {
        MyPagerAdapter(supportFragmentManager)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MemeItClient.Auth.isSignedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else if (!MemeItClient.Auth.isUserDataSaved()) {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                putExtra(AuthActivity.STARTING_MODE_PARAM, AuthActivity.MODE_PERSONALIZE)
            })
            finish()
        } else {
            initUI(savedInstanceState)
        }
    }

    private fun initUI(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        initToolbar()
        initBottomNav()
        main_viewpager.adapter = pagerAdapter
        val sbd = SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.65f)
                .withMenuLayout(R.layout.drawer_menu_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbd.withToolbarMenuToggle(toolbar2)
        }
        val rootNav = sbd.inject()
        findViewById<View>(R.id.aboutus).setOnClickListener {
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            rootNav.closeMenu(false)
        }
        findViewById<View>(R.id.settings).setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            rootNav.closeMenu(false)
        }
        findViewById<View>(R.id.menu_feedback).setOnClickListener {
            onFeedbackMenu()
            rootNav.closeMenu(false)
        }
        findViewById<View>(R.id.menu_invite).setOnClickListener {
            val intent = AppInviteInvitation.IntentBuilder("MemeIt")
                    .setMessage("Hey , It's ${MemeItClient.myUser!!.name},let make and share memes on MemeIt!" +
                            "download it here ")
                    .setDeepLink(Uri.parse("https://memeitapp.com"))
                    .setCallToActionText("Download")
                    .setEmailHtmlContent("")
                    .build()
            startActivityForResult(intent, 101)
            rootNav.closeMenu(false)
        }
        changeStatColor()
    }

    abstract fun onFeedbackMenu()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selected = savedInstanceState.getInt("selected")
        bottom_nav.select(selected, true)
    }

    private fun initToolbar() {
        this.setSupportActionBar(toolbar2)
    }

    private fun initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (searchItem != null && searchItem!!.isActionViewExpanded)
                searchItem!!.collapseActionView()
            appbar.setExpanded(true, false)
            when (item.itemId) {
                R.id.menu_home -> {
                    setTitle(0)
                    main_viewpager.setCurrentItem(0, false)
                    supportActionBar!!.show()
                    changeStatColor()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_trending -> {
                    setTitle(1)
                    main_viewpager.setCurrentItem(1, false)
                    supportActionBar!!.show()
                    changeStatColor()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_create -> {
                    startActivity(Intent(this@MainActivity, MemeChooserActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_favorites -> {
                    setTitle(2)
                    changeStatColor()
                    supportActionBar!!.show()
                    main_viewpager.setCurrentItem(2, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_me -> {
                    changeStatColor(true)

                    supportActionBar!!.hide()
                    main_viewpager.setCurrentItem(3, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun changeStatColor(profile: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = if (profile) Color.RED else Color.parseColor("#eeeeee")

        }
    }

    override fun setTitle(index: Int) {
        supportActionBar!!.title = titles[index]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_top_menu, menu)

        notifItem = menu.findItem(R.id.menu_notif)
        loadNotifCount()

        return super.onCreateOptionsMenu(menu)
    }

    private fun loadNotifCount() {
        MemeItUsers.getNotifCount().call {
            notifItem?.icon = DrawableBadge.Builder(this)
                    .drawableResId(R.drawable.ic_notifications_black_24dp)
                    .maximumCounter(99)
                    .build()
                    .get(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_notif -> startActivity(Intent(this, NotificationActivity::class.java))
            R.id.menu_search -> startActivity(Intent(this, SearchActivity::class.java))
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        loadNotifCount()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selected", bottom_nav.selectedIndex)
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = when (position) {
            0 -> MemeListFragment.newInstance(MemeAdapter.HOME_ADAPTER, HomeMemeLoader())
            1 -> MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, TrendingMemeLoader())
            2 -> MemeListFragment.newInstance(MemeAdapter.LIST_FAVORITE_ADAPTER, FavoriteMemeLoader())
            3 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }

        override fun getCount() = 4
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemeEditorActivity.REQUEST_CODE && resultCode == MemeEditorActivity.RESULT_CODE_SUCCESS_MEME) {
            startActivity(Intent(context, MemePosterActivity::class.java).apply {
                putExtras(data!!.extras!!)
            })
        }
    }
}