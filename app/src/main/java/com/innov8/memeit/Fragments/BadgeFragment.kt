package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.innov8.memeit.Adapters.BadgeAdapter
import com.innov8.memeit.R
import kotlinx.android.synthetic.main.fragment_badge.*

class BadgeFragment : Fragment() {

    lateinit var badgeAdapter: BadgeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    }


}
