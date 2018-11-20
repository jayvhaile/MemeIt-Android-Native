package com.innov8.memeit.CustomClasses

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.dimen
import com.innov8.memeit.screenWidth

class ProfileNameViewBehavior : CoordinatorLayout.Behavior<TextView> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    private val expandedTextSize = R.dimen.profile_name_expanded_textsize.dimen()
    private val collapsedTextSize = R.dimen.profile_name_collapsed_textsize.dimen()
    private val textSizeDiff = expandedTextSize - collapsedTextSize

    private val density = MemeItApp.instance.resources.displayMetrics.scaledDensity
    private val margin = R.dimen.profile_image_name_margin.dimen()


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

    override fun layoutDependsOn(parent: CoordinatorLayout, child: TextView, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    internal var textColor = 0

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: TextView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            if (appBarStartingHeight == 0) appBarStartingHeight = dependency.height
            val appBarCurrentDiff = dependency.bottom - appBarMinHeight
            val appBarRatio = appBarCurrentDiff / appBarDiff


            val dependencySize = collapsedSize + (appBarRatio * sizeDiff)
            val dependencyCenterX = collapsedX + (appBarRatio * xDiff) + dependencySize / 2f
            val dependencyCenterY = collapsedY + (appBarRatio * yDiff) + dependencySize / 2f
            val cw = child.width.toFloat()
            val ch = child.height.toFloat()


            val currentTextSize = collapsedTextSize + (appBarRatio * textSizeDiff)
            child.textSize = currentTextSize / density

            var angle = (1 - appBarRatio) * 90.0
            val c = if (angle > 72) Color.WHITE else Color.BLACK

            if (textColor != c) {
                textColor = c
                child.setTextColor(textColor)
            }

            angle = angle * Math.PI / 180
            val cr = (Math.sin(angle) * cw / 2 + Math.cos(angle) * ch / 2).toFloat()
            val m = (margin - margin * 0.5f * Math.cos(angle)).toFloat()
            val radius = dependencySize / 2f + m + cr

            val tcx = (Math.sin(angle) * radius).toFloat()
            val tcy = (Math.cos(angle) * radius).toFloat()


            child.x = dependencyCenterX + tcx - child.width / 2
            child.y = dependencyCenterY + tcy - child.height / 2

        }
        return false
    }


}