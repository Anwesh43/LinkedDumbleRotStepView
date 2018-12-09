package com.anwesh.uiprojects.dumblerotstepview

/**
 * Created by anweshmishra on 09/12/18.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.*

val nodes : Int = 5
val lines : Int = 4
val sizeFactor : Float = 2.4f
val scDiv : Double = 0.51
val scGap : Float = 0.05f
val hSize : Float = 0.8f
val color : Int = Color.parseColor("#0D47A1")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), this - n.getInverse() * i)

fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.mirrorValue(a : Int, b : Int) : Float = (1 - scaleFactor()) * a.getInverse() + scaleFactor() * b.getInverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Canvas.drawDRSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    val rectH : Float = hSize * size
    val triSize : Float = size - rectH
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.color = color
    save()
    translate(gap * (i + 1), h/2)
    rotate(sc2 * 90f)
    drawRect(RectF(-size, -triSize, size, triSize), paint)
    for (j in 0..(lines - 1)) {
        val sc : Float = sc1.divideScale(j, lines)
        val dy : Float = size
        val oy : Float = rectH
        val sy : Float = oy + (dy - oy) * sc
        val path: Path = Path()
        path.moveTo(rectH, -rectH)
        path.lineTo(size, -rectH)
        path.lineTo(size, -sy)
        path.lineTo(rectH, -rectH)
        drawPath(path, paint)
    }
    restore()
}

class DumbleRotStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float  = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateScale(dir, lines, 1)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class DRSNode(var i : Int = 0, val state : State = State()) {

        private var next : DRSNode? = null
        private var prev : DRSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if ( i < nodes - 1) {
                next = DRSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawDRSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DRSNode {
            var curr : DRSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class DumbleRotStep(var i : Int) {
        private val root : DRSNode = DRSNode()
        private var curr : DRSNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : DumbleRotStepView) {
        private var drs : DumbleRotStep = DumbleRotStep(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            drs.draw(canvas, paint)
            animator.animate {
                drs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            drs.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : DumbleRotStepView {
            val view : DumbleRotStepView = DumbleRotStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}