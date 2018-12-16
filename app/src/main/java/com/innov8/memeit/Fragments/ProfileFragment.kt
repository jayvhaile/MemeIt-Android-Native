package com.innov8.memeit.Fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.innov8.memeit.Activities.ProfileSettingsActivity
import com.innov8.memeit.Activities.TagsActivity
import com.innov8.memeit.Activities.UserListActivity
import com.innov8.memeit.Activities.UserTagActivity
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.Loaders.FollowerLoader
import com.innov8.memeit.Loaders.FollowingLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.loadImage
import com.innov8.memeit.Utils.prefix
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.User
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.loading_view_layout.*
import kotlinx.android.synthetic.main.profile_content.*


class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userID: String
    private var userData: User? = null

    private var username: String? = null
    private val isMe: Boolean
        get() = userID == MemeItClient.myUser?.id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myID = MemeItClient.myUser?.id
        username = arguments?.getString("username")
        if (username == null) {
            userID = arguments?.getString("uid", myID!!) ?: myID!!
            userData = arguments?.getParcelable("user")
        }
        loadData()
        retainInstance = true
    }

    private fun loadData() {
        val onLoaded = { user: User ->
            userData = user
            updateView(user)
        }
        val onError: (String) -> Unit = { context?.toast("Failed to load User Data:- $it") }
        if (username != null)
            MemeItUsers.getUserById(username!!).call({
                loader_view?.setLoaded()
                initPager()
                onLoaded(it)
            }, {
                loader_view?.setError("Couldn't load user profile")
            })
        else if (isMe)
            MemeItUsers.getMyUser(onLoaded, onError)
        else
            MemeItUsers.getUserById(userID).call(onLoaded, onError)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.loading_view_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader_view.setContentView(R.layout.fragment_profile)
        loader_view.setErrorText("Failed to load user profile")
        loader_view.onRetry = { loadData() }


        if (username == null || userData != null) {
            loader_view.setLoaded()
            initPager()
        }


        initTab()
        initListeners()
        toolbar.inflateMenu(R.menu.profile_page_menu)
        if (!isMe) toolbar.menu.removeItem(R.id.menu_profile_id)
        profile_follow_btn.visibility = if (isMe) GONE else VISIBLE
        if (isMe) updateView(MemeItClient.myUser!!.toUser())
        else if (userData != null) updateView(userData!!)

        loadData()
    }

    private var pagerInit = false
    private fun initPager() {
        if (pagerInit) return
        profile_viewpager.adapter = ViewPagerAdapter(childFragmentManager)
        pagerInit = true
    }

    private fun initTab() {
        tabs_profile.setupWithViewPager(profile_viewpager)
        listOf(
                R.drawable.ic_grid,
                R.drawable.ic_list,
                R.drawable.ic_badges
        ).forEachIndexed { index, id ->
            tabs_profile.getTabAt(index)?.apply {
                setCustomView(R.layout.tab_icon)
                (customView as ImageView).apply {
                    setImageResource(id)
                }
            }
        }
    }

    private fun initListeners() {
        val onFollowerClicked = { _: View -> UserListActivity.startActivity(context!!, FollowerLoader(userID)) }
        profile_followers.setOnClickListener(onFollowerClicked)
        profile_followers_count.setOnClickListener(onFollowerClicked)

        val onFollowingClicked = { _: View -> UserListActivity.startActivity(context!!, FollowingLoader(userID)) }
        profile_followings.setOnClickListener(onFollowingClicked)
        profile_followings_count.setOnClickListener(onFollowingClicked)

        profile_follow_btn.setOnClickListener { _ ->
            if (profile_follow_btn.text == "Follow") {
                MemeItUsers.followUser(userID).call({
                    profile_follow_btn.text = "Unfollow"
                    context?.toast("Followed")
                }, {
                    context?.toast("Failed to Follow:- $it")
                })
            } else if (profile_follow_btn.text == "Unfollow") {
                MemeItUsers.unfollowUser(userID).call({
                    profile_follow_btn.text = "Follow"
                    context?.toast("Unfollowed")
                }, {
                    context?.toast("Failed to unfollow:- $it")
                })
            }
        }
        toolbar.setOnMenuItemClickListener(this)
        tabs_profile.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as ImageView).setColorFilter(Color.GRAY)

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as ImageView).setColorFilter(Color.parseColor("#ff5e00"))
            }
        })
    }


    private fun updateView(user: User) {
        context?.run {
            profile_name?.text = user.name
            profile_username?.text = user.username
            profile_bio?.text = user.bio
            profile_image?.setText(user.name.prefix())
            profile_image?.loadImage(user.imageUrl)
            profile_followers_count?.text = CustomMethods.formatNumber(user.followerCount)
            profile_followings_count?.text = CustomMethods.formatNumber(user.followingCount)
            profile_meme_count?.text = CustomMethods.formatNumber(user.postCount)
            profile_follow_btn?.text = if (user.isFollowedByMe) "Unfollow" else "Follow"
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile_tag -> {
                if (isMe)
                    startActivity(Intent(context, TagsActivity::class.java))
                else {
                    val intent = Intent(context, UserTagActivity::class.java)
                    intent.putExtra("uid", userData!!.uid)
                    startActivity(intent)
                }
                return true
            }
            R.id.menu_profile_id -> {
                startActivity(Intent(context, ProfileSettingsActivity::class.java))
            }
        }
        return false
    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        //        var titles = arrayOf("Following", "Followers", "Memes", "Memes", "Badges")
        var fragments: Array<Fragment> = arrayOf(
                MemeListFragment.newInstanceForUserPosts(userID,
                        if (isMe)
                            MemeAdapter.GRID_ADAPTER_MY_POSTS
                        else
                            MemeAdapter.GRID_ADAPTER_USER_POSTS),
                MemeListFragment.newInstanceForUserPosts(userID,
                        if (isMe)
                            MemeAdapter.LIST_ADAPTER_MY_POSTS
                        else
                            MemeAdapter.LIST_ADAPTER_USER_POSTS),
                BadgeFragment.newInstance(if (isMe) null else userID))

        override fun getItem(position: Int): Fragment = fragments[position]


        override fun getCount(): Int = fragments.size
        override fun getPageTitle(position: Int): CharSequence? = null


    }

    companion object {

        fun byID(uid: String): ProfileFragment {
            val pf = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("uid", uid)
            pf.arguments = bundle
            return pf
        }

        fun byUsername(username: String): ProfileFragment {
            val pf = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            pf.arguments = bundle
            return pf
        }

        fun byUser(user: User): ProfileFragment {
            val pf = ProfileFragment()
            val bundle = Bundle()
            bundle.putParcelable("user", user)
            bundle.putString("uid", user.uid)
            pf.arguments = bundle
            return pf
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
