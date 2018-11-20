package com.innov8.memeit.CustomClasses

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.innov8.memeit.R
import com.innov8.memeit.dimen
import com.innov8.memeit.screenWidth

class ProfileImageViewBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    private val expandedSize = R.dimen.profile_image_expanded_size.dimen()
    private val expandedX = (screenWidth - expandedSize) / 2f
    private val expandedY = R.dimen.profile_cover_expanded_height.dimen() - expandedSize / 2f

    private val collapsedSize = R.dimen.profile_image_collapsed_size.dimen()
    private val collapsedX = R.dimen.profile_image_collapsed_X.dimen()
    private val collapsedY = collapsedX

    private val sizeDiff = expandedSize - collapsedSize
    private val xDiff = expandedX - collapsedX
    private val yDiff = expandedY - collapsedY

    private var appBarStartingHeight = 0
    private val appBarMinHeight = R.dimen.profile_toolbar_height.dimen() +
            R.dimen.profile_tab_height.dimen()
    private val appBarDiff get() = appBarStartingHeight - appBarMinHeight

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            if (appBarStartingHeight == 0) appBarStartingHeight = dependency.height
            val appBarCurrentDiff = dependency.bottom - appBarMinHeight
            val appBarRatio = appBarCurrentDiff / appBarDiff

            val currentSize = collapsedSize + (appBarRatio * sizeDiff)
            val currentX = collapsedX + (appBarRatio * xDiff)
            val currentY = collapsedY + (appBarRatio * yDiff)
            val s = currentSize.toInt()

            child.x = currentX
            child.y = currentY
            child.layoutParams = child.layoutParams.apply {
                width = s
                height = s
            }
        }
        return false
    }
}

