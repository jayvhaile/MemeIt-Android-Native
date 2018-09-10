package com.innov8.memeit.CustomClasses

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.Log
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.innov8.memegenerator.utils.fromDPToPX

//todo change the px to dp
class LoadingDrawable (val context:Context): ProgressBarDrawable() {
    override fun onLevelChange(level: Int): Boolean {
        super.onLevelChange(level)
        if (level>10000)return false
        sweepAngle=sweepInitialAngle+(360f-sweepInitialAngle)*(level/10000f)
        Log.d("fuck",level.toString()+"\t"+(360f-sweepInitialAngle)*(level/10000f))
        invalidateSelf()
        return true
    }
    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
    var startingAngle=0f
        set(value) {
            field=value%360
        }

    var c:Long=0

    val radius=16f.fromDPToPX(context)
    val strokeSize=2f.fromDPToPX(context)
    val paddingSize=1f.fromDPToPX(context)
    val sweepInitialAngle=20f
    var sweepAngle=sweepInitialAngle
    val paint:Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val paint2:Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val anim=ValueAnimator.ofFloat(0f,0.1f)
    val anim2=ValueAnimator.ofFloat(0f,0.1f)
    init {
        paint.color=Color.WHITE
        paint.style=Paint.Style.STROKE
        paint.strokeWidth=strokeSize

        paint2.color=Color.argb(80,0,0,0)

        anim.duration=1
        anim.repeatMode=ValueAnimator.RESTART
        anim.repeatCount=ValueAnimator.INFINITE
        anim.addUpdateListener {
            startingAngle+=4.5f
            invalidateSelf()
        }
        anim.start()
    }


    override fun draw(canvas: Canvas) {
        val w=bounds.width()/2f
        val h=bounds.height()/2f

        canvas.drawCircle(w,h,radius+strokeSize+paddingSize,paint2)
        val rect=RectF(w-radius,h-radius,w+radius,h+radius)
        canvas.drawArc(rect,startingAngle,sweepAngle,false,paint)

    }
}