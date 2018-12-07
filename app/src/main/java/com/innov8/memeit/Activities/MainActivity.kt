package com.innov8.memeit.Activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomClasses.MyFragmentPagerAdapter
import com.innov8.memeit.CustomViews.SearchToolbar
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.Fragments.ProfileFragment
import com.innov8.memeit.Loaders.FavoriteMemeLoader
import com.innov8.memeit.Loaders.HomeMemeLoader
import com.innov8.memeit.Loaders.TrendingMemeLoader
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.minibugdev.drawablebadge.DrawableBadge
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val titles = arrayOf("Home", "Trending", "Favorites")
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
                .withMenuLayout(R.layout.drawer_main2)
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
            startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
            rootNav.closeMenu(false)
        }
        findViewById<View>(R.id.menu_invite).setOnClickListener {
            val intent = AppInviteInvitation.IntentBuilder("MemeIt")
                    .setMessage("the world of meme")
                    .setDeepLink(Uri.parse("meme"))
                    .setCallToActionText("download")
                    .build()
            startActivityForResult(intent, 101)
            rootNav.closeMenu(false)
        }
    }

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
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_trending -> {
                    setTitle(1)
                    main_viewpager.setCurrentItem(1, false)
                    supportActionBar!!.show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_create -> {
                    startActivity(Intent(this@MainActivity, MemeChooser::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_favorites -> {
                    setTitle(2)
                    supportActionBar!!.show()
                    main_viewpager.setCurrentItem(2, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_me -> {
                    supportActionBar!!.hide()
                    main_viewpager.setCurrentItem(3, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun setTitle(index: Int) {
        supportActionBar!!.title = titles[index]
    }

    private val frag
        get() = supportFragmentManager.findFragmentByTag(pagerAdapter.getItemTag(main_viewpager.currentItem))

    private fun showSearch() {
        val frag = frag as MemeListFragment
        frag.setSearchMode(true)
    }

    private fun closeSearch() {
        val frag = frag as MemeListFragment
        frag.setSearchMode(false)
    }

    private val searchToolbar by lazy {
        searchItem!!.actionView as SearchToolbar
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_top_menu, menu)
        searchItem = menu.findItem(R.id.menu_search)
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                showSearch()
                return searchToolbar.onMenuItemActionExpand(item)
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                closeSearch()
                return searchToolbar.onMenuItemActionCollapse(item)
            }
        })
        searchToolbar.OnSearch = { s, strings ->
            val frag = frag as MemeListFragment
            frag.search(s, strings)
        }
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

    private inner class MyPagerAdapter(fm: FragmentManager) : MyFragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = when (position) {
            0 -> MemeListFragment.newInstance(MemeAdapter.HOME_ADAPTER, HomeMemeLoader())
            1 -> MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, TrendingMemeLoader())
            2 -> MemeListFragment.newInstance(MemeAdapter.LIST_FAVORITE_ADAPTER, FavoriteMemeLoader())
            3 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }

        override fun getCount() = 4
    }
}