package com.innov8.memegenerator.MemeEngine

import android.graphics.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.innov8.memegenerator.utils.Action
import com.innov8.memegenerator.utils.ActionManager
import com.innov8.memeit.commons.dp

class PaintHandler(val view: View) {
    enum class PaintMode {
        DOODLE {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = Doodle(paintProperty)

        },
        LINE {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = Line(paintProperty)

        },
        ARROW {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = Arrow(paintProperty)

        },
        RECT {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = RectPaint(paintProperty)
        },
        CIRCLE {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = CirclePaint(paintProperty)
        },
        OVAL {
            override fun makePaintable(paintProperty: PaintProperty): TouchPaintable = OvalPaint(paintProperty)
        };

        abstract fun makePaintable(paintProperty: PaintProperty): TouchPaintable
    }

    class PaintAction(val paintHandler: PaintHandler, val paintable: TouchPaintable) : Action("") {
        override fun `do`() {
            paintHandler.addPaintable(paintable, false)
        }

        override fun undo() {
            paintHandler.removePaintable(paintable)
        }

    }

    val paintables = mutableListOf<TouchPaintable>()


    var paintMode = PaintMode.DOODLE
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //    val b = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
    var paintProperty = PaintProperty(Color.BLACK, 4f.dp(view.context))

    var actionManager = ActionManager()

    init {
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.xfermode = null
        paint.alpha = 0xff
        backgroundPaint.color = Color.BLACK
    }

    fun addPaintable(paintable: TouchPaintable, registerAction: Boolean = true) {
        paintables.add(paintable)
        if (registerAction) actionManager.pushAction(PaintAction(this, paintable))
        view.invalidate()
    }

    fun removePaintable(paintable: TouchPaintable) {
        paintables.remove(paintable)
        view.invalidate()
    }


    private val detector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
        lateinit var touchPaintable: TouchPaintable
        override fun onDown(e: MotionEvent): Boolean {
            touchPaintable = paintMode.makePaintable(paintProperty)
            touchPaintable.start(e)
            addPaintable(touchPaintable)
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            touchPaintable.update(e2)
            view.invalidate()
            return true
        }

    })

    fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event)
    }

    fun draw(canvas: Canvas) {
        //canvas.drawRect(0f, 0f, view.width.toFloat(), view.height.toFloat(), backgroundPaint)
        paintables.forEach { p ->
            p.paintProperty.apply(paint)
            p.paint(canvas, paint)
        }
    }
}

interface Paintable {
    var paintProperty: PaintProperty

    fun paint(canvas: Canvas, paint: Paint)
}

interface TouchPaintable : Paintable {
    fun start(event: MotionEvent)
    fun update(event: MotionEvent)

}

data class PaintProperty(val color: Int, val brushSize: Float, val maskFilter: MaskFilter? = null) {
    fun apply(paint: Paint) {
        paint.color = color
        paint.strokeWidth = brushSize
        paint.maskFilter = maskFilter
    }
}

class Doodle(override var paintProperty: PaintProperty) : TouchPaintable {
    val path: Path = Path()


    override fun start(event: MotionEvent) {
        path.moveTo(event.x, event.y)
    }

    override fun update(event: MotionEvent) {
        path.lineTo(event.x, event.y)
    }

    override fun paint(canvas: Canvas, paint: Paint) {
        canvas.drawPath(path, paint)
    }
}

class Line(override var paintProperty: PaintProperty) : TouchPaintable {
    val start = PointF()
    val end = PointF()


    override fun start(event: MotionEvent) {
        start.x = event.x
        start.y = event.y
        end.x = event.x
        end.y = event.y
    }

    override fun update(event: MotionEvent) {
        end.x = event.x
        end.y = event.y
    }

    override fun paint(canvas: Canvas, paint: Paint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
}

class Arrow(override var paintProperty: PaintProperty) : TouchPaintable {
    val start = PointF()
    val end = PointF()

    val sweepAngle = 45.toRad()

    override fun start(event: MotionEvent) {
        start.x = event.x
        start.y = event.y
        end.x = event.x
        end.y = event.y
    }

    override fun update(event: MotionEvent) {
        end.x = event.x
        end.y = event.y
    }


    override fun paint(canvas: Canvas, paint: Paint) {
        val angle = end.angle(start)
        val l = start.distance(end) * 0.2f
        val a1x = end.x + (l * Math.cos(angle - sweepAngle))
        val a1y = end.y + (l * Math.sin(angle - sweepAngle))
        val a2x = end.x + (l * Math.cos(angle + sweepAngle))
        val a2y = end.y + (l * Math.sin(angle + sweepAngle))

        canvas.drawLine(start.x, start.y, end.x, end.y, paint)

//        paint.strokeWidth=paint.strokeWidth*0.8f
        canvas.drawLine(a1x.toFloat(), a1y.toFloat(), end.x, end.y, paint)
        canvas.drawLine(a2x.toFloat(), a2y.toFloat(), end.x, end.y, paint)

    }
}

fun Int.toRad() = Math.toRadians(this.toDouble())

class RectPaint(override var paintProperty: PaintProperty) : TouchPaintable {
    val rectF = RectF()


    override fun start(event: MotionEvent) {
        rectF.left = event.x
        rectF.right = event.x + 10
        rectF.top = event.y
        rectF.bottom = event.y + 10

    }

    override fun update(event: MotionEvent) {
        rectF.right = event.x
        rectF.bottom = event.y
    }

    override fun paint(canvas: Canvas, paint: Paint) {
        canvas.drawRect(rectF, paint)
    }
}

class CirclePaint(override var paintProperty: PaintProperty) : TouchPaintable {
    var radius = 0f
    val center = PointF()
    override fun start(event: MotionEvent) {
        center.x = event.x
        center.y = event.y
        radius = 10f
    }

    override fun update(event: MotionEvent) {
        radius = center.distance(event.x, event.y)
    }

    override fun paint(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(center.x, center.y, radius, paint)
    }
}

class OvalPaint(override var paintProperty: PaintProperty) : TouchPaintable {
    val rectF = RectF()


    override fun start(event: MotionEvent) {
        rectF.left = event.x
        rectF.right = event.x + 10
        rectF.top = event.y
        rectF.bottom = event.y + 10

    }

    override fun update(event: MotionEvent) {

        rectF.right = event.x
        rectF.bottom = event.y
    }


    override fun paint(canvas: Canvas, paint: Paint) {
        canvas.drawOval(rectF, paint)
    }
}

fun PointF.distance(p2: PointF) = this.distance(p2.x, p2.y)
fun PointF.distance(x2: Float, y2: Float) = ((x2 - x).sqr + (y2 - y).sqr).sqrt

fun PointF.angle(p2: PointF): Float = Math.atan2((p2.y - y).toDouble(), (p2.x - x).toDouble()).toFloat()

val Float.sqr: Float
    get() = this * this


val Float.sqrt: Float
    get() = Math.sqrt(this.toDouble()).toFloat()