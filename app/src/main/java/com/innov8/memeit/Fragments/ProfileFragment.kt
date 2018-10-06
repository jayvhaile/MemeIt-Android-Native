package com.innov8.memeit.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Activities.TagsActivity
import com.innov8.memeit.Activities.UserTagActivity
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.CustomClasses.UserListLoader
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.memeit.backend.dataclasses.User
import com.memeit.backend.kotlin.MemeItUsers
import com.memeit.backend.kotlin.call
import kotlinx.android.synthetic.main.profile_page.*


class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userID: String
    internal var size: Float = 0.toFloat()

    private var userData: User? = null

    private val isMe: Boolean
        get() = userID == MemeItUsers.getMyUser()?.userID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myID = MemeItUsers.getMyUser()!!.userID!!
        userID = arguments?.getString("uid", myID) ?: myID
        userData = arguments?.getParcelable("user")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerAdapter(childFragmentManager)
        profile_viewpager.adapter = adapter
        tabs_profile.setupWithViewPager(profile_viewpager)
        val t = view.findViewById(R.id.toolbar)
        t.inflateMenu(R.menu.profile_page_menu)
        t.setOnMenuItemClickListener(this)

        profile_follow_btn.visibility = if (isMe) GONE else VISIBLE

        profile_follow_btn.setOnClickListener { view ->
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
        size = resources.getDimension(R.dimen.image_width)
        if (isMe) loadFast()
        else if (userData != null) updateView()
        loadData()
    }

    private fun loadFast() {
        val myUser = MemeItUsers.getMyUser()
        profile_name.text = myUser!!.name
        profile_image.text = myUser.name.prefix()
        profile_image.loadImage(myUser.imageUrl, size, size)

    }

    private fun loadData() {
        val onLoaded = { user: User ->
            userData = user
            updateView()
        }
        val onError:(String)->Unit = {context?.toast("Failed to load User Data:- $it") }
        if (isMe)
            MemeItUsers.getMyUser(onLoaded, onError)
        else
            MemeItUsers.getUserById(userID).call(onLoaded, onError)

    }

    private fun updateView() {
        profile_name.text = userData!!.name
        profile_image.text = userData!!.name.prefix()
        profile_image.loadImage(userData!!.imageUrl, size, size)
        profile_followers_count.text = CustomMethods.formatNumber(userData!!.followerCount)
        profile_meme_count.text = CustomMethods.formatNumber(userData!!.postCount)
        profile_follow_btn.text = if (userData?.isFollowedByMe == true) "Unfollow" else "Follow"
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile_tag -> {
                if (isMe)
                    startActivity(Intent(context, TagsActivity::class.java))
                else {
                    val intent = Intent(context, UserTagActivity::class.java)
                    intent.putExtra("uid", userData!!.userID)
                    startActivity(intent)
                }
                return true
            }
        }
        return false
    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        var titles = arrayOf("MemeItMemes", "Following", "Followers")
        var fragments: Array<Fragment> = arrayOf(MemeListFragment.newInstanceForUserPosts(userID),
                UserListFragment.newInstance(UserListLoader.FOLLOWING_LOADER, userID),
                UserListFragment.newInstance(UserListLoader.FOLLOWER_LOADER, userID))

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    companion object {

        fun newInstance(uid: String): ProfileFragment {
            val pf = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("uid", uid)
            pf.arguments = bundle
            return pf
        }

        fun newInstance(user: User): ProfileFragment {
            val pf = ProfileFragment()
            val bundle = Bundle()
            bundle.putParcelable("user", user)
            bundle.putString("uid", user.userID)
            pf.arguments = bundle
            return pf
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
