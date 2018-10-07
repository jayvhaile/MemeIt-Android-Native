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
import com.innov8.memeit.CustomClasses.FollowerLoader
import com.innov8.memeit.CustomClasses.FollowingLoader
import com.innov8.memeit.loadImage
import com.innov8.memeit.prefix
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.dataclasses.User
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.profile_page.*


class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userID: String
    internal var size: Float = 0.toFloat()

    private var userData: User? = null

    private val isMe: Boolean
        get() = userID == MemeItClient.myUser?.id


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myID = MemeItClient.myUser?.id
        userID = arguments?.getString("uid", myID!!) ?: myID!!
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

        toolbar.inflateMenu(R.menu.profile_page_menu)
        toolbar.setOnMenuItemClickListener(this)

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
        val myUser = MemeItClient.myUser
        profile_name.text = myUser!!.name
        profile_image.text = myUser.name.prefix()
        profile_image.loadImage(myUser.profilePic, size, size)

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
                    intent.putExtra("uid", userData!!.uid)
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
                UserListFragment.newInstance(FollowingLoader(userID)),
                UserListFragment.newInstance(FollowerLoader(userID)))

        override fun getItem(position: Int): Fragment = fragments[position]


        override fun getCount(): Int=fragments.size


        override fun getPageTitle(position: Int): CharSequence?=titles[position]
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
            bundle.putString("uid", user.uid)
            pf.arguments = bundle
            return pf
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
