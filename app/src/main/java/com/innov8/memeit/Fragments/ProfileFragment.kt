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
import com.memeit.backend.models.UserReq
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.loading_view_layout.*
import kotlinx.android.synthetic.main.profile_content.*


class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userData: User

    private val isMe: Boolean
        get() = userData.uid == MemeItClient.myUser?.id

    private fun getMyUser() = MemeItClient.myUser!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userData = arguments?.getParcelable("user")!!
        retainInstance = true
        initUser()
    }

    private fun initUser() {
        if (userData.uid == null) {
            if (userData.username != null) {
                if (userData.username == getMyUser().username) {
                    userData = getMyUser().toUser()
                    needToRefresh = true
                    loadedState = loaded
                    initViews()
                } else {
                    MemeItUsers.getUserByUserName(userData.username!!).call({
                        userData = it
                        needToRefresh = false
                        loadedState = loaded
                        initViews()
                    }) {
                        loadedState = failed
                        loader_view?.setError("Failed to load User Data")
                    }
                }
            }
        } else {
            if (userDataCompelete()) {
                needToRefresh = true
                loadedState = loaded
                initViews()
            } else {
                MemeItUsers.getUserById(userData.uid!!).call({
                    userData = it
                    needToRefresh = false
                    loadedState = loaded
                    initViews()
                }) {
                    loadedState = failed
                    loader_view?.setError("Failed to load User Data")
                }
            }
        }
    }

    private fun userDataCompelete() =
            userData.run {
                username != null && name != null
            }


    private fun refreshData() {
        val onLoaded = { user: User ->
            userData = user
            updateView()
        }
        val onError: (String) -> Unit = { context?.toast("Failed to refresh User Data") }

        if (isMe)
            MemeItUsers.getMyUser(onLoaded, onError)
        else
            MemeItUsers.getUserById(userData.uid!!).call(onLoaded, onError)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.loading_view_layout, container, false)
    }

    val loading = 0
    val loaded = 1
    val failed = 2

    var needToRefresh = true
    var loadedState = 0
        set(value) {
            field = value
            if (field == loaded && needToRefresh) {
                refreshData()
            }
        }

    fun initViews() {
        if (view == null) return
        if (loadedState != loaded) throw IllegalStateException("data should be loaded")
        loader_view.setLoaded()
        profile_viewpager.adapter = ViewPagerAdapter(childFragmentManager)
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

        initListeners()
        toolbar.inflateMenu(R.menu.profile_page_menu)
        if (!isMe) toolbar.menu.removeItem(R.id.menu_profile_id)
        profile_follow_btn.visibility = if (isMe) GONE else VISIBLE
        updateView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader_view.setContentView(R.layout.fragment_profile)
        loader_view.setErrorText("Failed to load user profile")
        loader_view.onRetry = { initUser() }
        if (loadedState == loaded)
            initViews()


    }

    private fun initListeners() {
        val onFollowerClicked = { _: View -> UserListActivity.startActivity(context!!, FollowerLoader(userData.uid)) }
        profile_followers.setOnClickListener(onFollowerClicked)
        profile_followers_count.setOnClickListener(onFollowerClicked)

        val onFollowingClicked = { _: View -> UserListActivity.startActivity(context!!, FollowingLoader(userData.uid)) }
        profile_followings.setOnClickListener(onFollowingClicked)
        profile_followings_count.setOnClickListener(onFollowingClicked)

        profile_follow_btn.setOnClickListener { _ ->
            if (profile_follow_btn.text == "Follow") {
                MemeItUsers.followUser(userData.uid!!).call({
                    profile_follow_btn.text = "Unfollow"
                    context?.toast("Followed")
                }, {
                    context?.toast("Failed to Follow:- $it")
                })
            } else if (profile_follow_btn.text == "Unfollow") {
                MemeItUsers.unfollowUser(userData.uid!!).call({
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


    private fun updateView() {
        context?.run {
            profile_name?.text = userData.name
            profile_username?.text = userData.username
            profile_bio?.text = userData.bio
            profile_image?.setText(userData.name.prefix())
            profile_image?.loadImage(userData.imageUrl)
            profile_followers_count?.text = CustomMethods.formatNumber(userData.followerCount)
            profile_followings_count?.text = CustomMethods.formatNumber(userData.followingCount)
            profile_meme_count?.text = CustomMethods.formatNumber(userData.postCount)
            profile_follow_btn?.text = if (userData.isFollowedByMe) "Unfollow" else "Follow"
        }
    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile_tag -> {
                if (isMe)
                    startActivity(Intent(context, TagsActivity::class.java))
                else {
                    val intent = Intent(context, UserTagActivity::class.java)
                    intent.putExtra("uid", userData.uid)
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
                MemeListFragment.newInstanceForUserPosts(userData.uid!!,
                        if (isMe)
                            MemeAdapter.GRID_ADAPTER_MY_POSTS
                        else
                            MemeAdapter.GRID_ADAPTER_USER_POSTS),
                MemeListFragment.newInstanceForUserPosts(userData.uid!!,
                        if (isMe)
                            MemeAdapter.LIST_ADAPTER_MY_POSTS
                        else
                            MemeAdapter.LIST_ADAPTER_USER_POSTS),
                BadgeFragment.newInstance(if (isMe) null else userData.uid!!))

        override fun getItem(position: Int): Fragment = fragments[position]


        override fun getCount(): Int = fragments.size
        override fun getPageTitle(position: Int): CharSequence? = null


    }

    companion object {

        fun byID(uid: String): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user", User(uid = uid))
                }
            }
        }

        fun byUsername(username: String): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user", User(username = username))
                }
            }
        }

        fun byUser(user: User): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user", user)
                }
            }
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user", MemeItClient.myUser!!.toUser())
                }
            }
        }
    }
}
