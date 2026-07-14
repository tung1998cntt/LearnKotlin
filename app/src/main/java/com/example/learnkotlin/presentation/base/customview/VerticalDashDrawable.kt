package com.example.learnkotlin.presentation.base.customview

import android.graphics.*
import android.graphics.drawable.Drawable

class VerticalDashDrawable(
    color: Int,
    dashLength: Float,
    dashGap: Float,
    strokeWidth: Float
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        this.color = color
        this.strokeWidth = strokeWidth
        pathEffect = DashPathEffect(
            floatArrayOf(dashLength, dashGap),
            0f
        )
    }

    override fun draw(canvas: Canvas) {

        val x = bounds.width() / 2f

        canvas.drawLine(
            x,
            0f,
            x,
            bounds.height().toFloat(),
            paint
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}