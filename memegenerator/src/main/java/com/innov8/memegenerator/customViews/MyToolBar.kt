package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.innov8.memegenerator.utils.fromDPToPX

class MyToolBar : LinearLayout {
    lateinit var leftMenuHolder:LinearLayout
    lateinit var rightMenuHolder:LinearLayout
    var showText:Boolean=false
    lateinit var menusLayoutParams: LayoutParams
    var padding:Int=0
    constructor(context: Context) : super(context) {
        init(context)

    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }
    private fun init(context: Context, attrs: AttributeSet?=null) {
        orientation=LinearLayout.HORIZONTAL

        leftMenuHolder=LinearLayout(context)
        leftMenuHolder.orientation= HORIZONTAL
        leftMenuHolder.gravity=Gravity.LEFT
        rightMenuHolder= LinearLayout(context)
        rightMenuHolder.orientation= HORIZONTAL
        rightMenuHolder.gravity=Gravity.RIGHT

        val lp=LayoutParams(0,LayoutParams.MATCH_PARENT,1f)
        addView(leftMenuHolder,lp)
        addView(rightMenuHolder,lp)
        menusLayoutParams= LayoutParams(40f.fromDPToPX(context).toInt(),56f.fromDPToPX(context).toInt())

        padding=16f.fromDPToPX(context).toInt()
    }
    fun addLeftMenu(t:MyToolbarmenu){
        val item = generateItem(t)
        leftMenuHolder.addView(item,menusLayoutParams)
    }
    fun addRightMenu(t:MyToolbarmenu){
        val item = generateItem(t)
        rightMenuHolder.addView(item,menusLayoutParams)
    }
    fun setLeftMenus(leftMenus:List<MyToolbarmenu>){
        leftMenuHolder.removeAllViews()
        leftMenus.forEach {t->
            val item = generateItem(t)
            leftMenuHolder.addView(item,menusLayoutParams)
        }
    }
    fun setRightMenus(leftMenus:List<MyToolbarmenu>){
        rightMenuHolder.removeAllViews()
        leftMenus.forEach {t->
            val item = generateItem(t)
            rightMenuHolder.addView(item,menusLayoutParams)
        }
    }

    private fun generateItem(t: MyToolbarmenu): ImageView {
        val item = ImageView(context)
        item.setImageResource(t.drawableID)
        item.scaleType = ImageView.ScaleType.FIT_CENTER
        item.setOnClickListener({ t.onClick?.invoke() })
        item.setPadding(0,padding,0,padding)
        return item
    }


}
class MyToolbarmenu(val drawableID:Int,
                    var onClick:(()->Unit)?=null)