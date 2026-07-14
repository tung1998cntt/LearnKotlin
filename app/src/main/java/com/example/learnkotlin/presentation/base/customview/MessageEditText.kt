package com.example.learnkotlin.presentation.base.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText

class MessageEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val TEXT_SIZE_SP = 16f
        private const val MIN_LINES = 5
        private const val MAX_LINES_BEFORE_SCROLL = 10
    }

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        // Layout
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // ===== CHUẨN SCROLL 5 LINES =====
        minLines = MIN_LINES
        maxLines = MIN_LINES

        isVerticalScrollBarEnabled = true
        movementMethod = ScrollingMovementMethod.getInstance()
        overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS

        setOnTouchListener { v, _ ->
            v.parent?.requestDisallowInterceptTouchEvent(true)
            false
        }

        // Scroll bar
        scrollBarStyle = SCROLLBARS_INSIDE_INSET

        // Style mặc định
        if (attrs == null || hint == null) {
            hint = "Nhập nội dung tin nhắn..."
        }

        setHintTextColor(Color.parseColor("#94A3B8"))
        setTextColor(Color.parseColor("#1E2939"))
        textSize = TEXT_SIZE_SP
        setPadding(40, 40, 40, 40)
        gravity = Gravity.TOP or Gravity.START

        background = createBackground()

        filters = arrayOf(InputFilter.LengthFilter(1000))
    }

    private fun getLineHeightPx(): Float {
        val textSizePx = TEXT_SIZE_SP * resources.displayMetrics.scaledDensity
        return textSizePx * 1.2f
    }

    private fun createBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#144B465C"))
            setStroke(4, Color.parseColor("#D1D5DC"))
            cornerRadius = dpToPx(16).toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}