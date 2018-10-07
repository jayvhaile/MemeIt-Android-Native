package com.innov8.memeit.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Adapters.TagsAdapter
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.activity_tags.*
import kotlinx.android.synthetic.main.fragment_meme_list.*
import retrofit2.Call

class TagsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        val pager = Pager(supportFragmentManager)
        tags_pager.adapter = pager
        tags_tab.setupWithViewPager(tags_pager)
    }
    class Pager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val frags = listOf("Followed Tags" to TagFragment.newInstance(MyTagLoader()),
                "Popular Tags" to TagFragment.newInstance(PopularTagLoader()),
                "Trending Tags" to TagFragment.newInstance(TrendingTagLoader()))

        override fun getItem(position: Int): Fragment = frags[position].second
        override fun getCount(): Int = frags.size
        override fun getPageTitle(position: Int): CharSequence = frags[position].first
    }
}
class UserTagActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent?.getStringExtra("uid")
        if (id == null) {
            finish()
            return
        }
        uid = id
        name = intent?.getStringExtra("name") ?: "..."
        setContentView(R.layout.activity_user_tag)
        supportFragmentManager.beginTransaction().replace(R.id.holder, TagFragment.newInstance(UserTagLoader(uid)))
        setSupportActionBar(tags_toolbar)
        supportActionBar?.setTitle("Tags Followed by $name")
        if (name == "...")
            MemeItUsers.getUserById(uid).call {
                supportActionBar?.setTitle("Tags Followed by ${it.name}")
            }
    }
}
class TagsChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_tag)
        setSupportActionBar(tags_toolbar)
        supportActionBar?.setTitle("Choose Tags")
        supportFragmentManager.beginTransaction().replace(R.id.holder, TagFragment.newInstance(PopularTagLoader()))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tags_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tag_done -> {
                val i = Intent(this, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

class TagFragment : Fragment() {
    lateinit var tagLoader: TagLoader

    companion object {
        fun newInstance(loader: TagLoader): TagFragment {
            val fragment = TagFragment()
            val arg = Bundle()
            arg.putParcelable("loader", loader)
            fragment.arguments = arg
            return fragment
        }
    }

    lateinit var tagsAdapter: TagsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagLoader = arguments?.getParcelable("loader") ?: return
        tagsAdapter = TagsAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_recycler_view.adapter = tagsAdapter
        meme_recycler_view.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        swipe_to_refresh.setOnRefreshListener {
            load()
        }
        load()
    }

    private fun load() {
        swipe_to_refresh.isRefreshing = true
        tagLoader.loadTags(0, 300).call({
            tagsAdapter.setAll(it)
            swipe_to_refresh.isRefreshing = false
        }, {
            context?.toast("Error Loading Tags: $it")
        })
    }

}

interface TagLoader : Parcelable {
    companion object {
        val loaders = listOf(MyTagLoader, PopularTagLoader, TrendingTagLoader)
    }

    fun loadTags(skip: Int, limit: Int): Call<List<Tag>>
}

class MyTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this()

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItUsers.getMyTags(skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyTagLoader> {
        override fun createFromParcel(parcel: Parcel): MyTagLoader {
            return MyTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<MyTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}

class UserTagLoader(private val uid: String) : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItUsers.getTagsFor(uid, skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserTagLoader> {
        override fun createFromParcel(parcel: Parcel): UserTagLoader {
            return UserTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<UserTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}

class PopularTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getPopularTags(null, skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopularTagLoader> {
        override fun createFromParcel(parcel: Parcel): PopularTagLoader {
            return PopularTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<PopularTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}

class TrendingTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getTrendingTags(skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingTagLoader> {
        override fun createFromParcel(parcel: Parcel): TrendingTagLoader {
            return TrendingTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<TrendingTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}

class SuggestedTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getSuggestedTags(skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuggestedTagLoader> {
        override fun createFromParcel(parcel: Parcel): SuggestedTagLoader {
            return SuggestedTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<SuggestedTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}
