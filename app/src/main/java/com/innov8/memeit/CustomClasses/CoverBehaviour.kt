package com.innov8.memeit.CustomClasses

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.innov8.memeit.R
import com.innov8.memeit.dimen

class CoverBehaviour : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val height = R.dimen.profile_cover_expanded_height.dimen()
    private val expandedY = 0
    private val collapsedY = R.dimen.profile_toolbar_height.dimen() - height
    private val yDiff = expandedY - collapsedY

    private val appBarStartingHeight = R.dimen.profile_collapsing_toolbar_expanded_height.dimen() +
            R.dimen.profile_tab_height.dimen()
    private val appBarMinHeight = R.dimen.profile_toolbar_height.dimen() +
            R.dimen.profile_tab_height.dimen()
    private val appBarDiff = appBarStartingHeight - appBarMinHeight
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val appBarCurrentDiff = dependency.bottom - appBarMinHeight
            val appBarRatio = appBarCurrentDiff / appBarDiff
            child.y = collapsedY + (appBarRatio * yDiff)
        }

        return false
    }
}