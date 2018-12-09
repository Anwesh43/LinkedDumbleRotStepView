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
val strokeFactor : Int = 90
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

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}