package com.innov8.memegenerator.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class ViewAdapter(val context: Context) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getItem(position)
        container.addView(view)
        return view
    }

    abstract fun getItem(position: Int): View
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}