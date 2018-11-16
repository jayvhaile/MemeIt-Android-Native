package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.innov8.memeit.Adapters.BadgeAdapter
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.fragment_badge.*

class BadgeFragment : Fragment() {
    companion object {
        fun newInstance(uid: String?): BadgeFragment {
            val bf = BadgeFragment()
            val bundle = Bundle().apply {
                putString("uid", uid)
            }
            bf.arguments = bundle
            return bf
        }
    }

    lateinit var badgeAdapter: BadgeAdapter
    var uid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = arguments?.getString("uid")
        badgeAdapter = BadgeAdapter(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_badge, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lm = GridLayoutManager(context!!, badgeAdapter.mode)
        badge_list.layoutManager = lm
        badge_list.adapter = badgeAdapter
        badge_view_switch.setOnClickListener {
            if (badgeAdapter.mode == BadgeAdapter.MODE_GRID) {
                lm.spanCount = 1
                badgeAdapter.mode = BadgeAdapter.MODE_LIST
                badgeAdapter.notifyItemRangeChanged(0, badgeAdapter.itemCount)
            } else {
                lm.spanCount = 5
                badgeAdapter.mode = BadgeAdapter.MODE_GRID
                badgeAdapter.notifyItemRangeChanged(0, badgeAdapter.itemCount)
            }
        }
        load()

    }

    private fun load() {
        if (uid == null) {
            badgeAdapter.setAll(MemeItClient.myUser!!.badges)
            MemeItUsers.getMyBadges({
                badgeAdapter.setAll(it)
            })
        } else
            MemeItUsers.getBadgesFor(uid!!).call {
                badgeAdapter.setAll(it)
            }
    }


}
