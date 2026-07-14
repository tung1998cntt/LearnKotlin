package com.example.learnkotlin.presentation.base.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.learnkotlin.R
import com.example.learnkotlin.databinding.LayoutSegmentedControlBinding

class SegmentedControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val binding = LayoutSegmentedControlBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var selectedIndex = 0

    private var listener: ((Int) -> Unit)? = null

    private var selectedColor = Color.RED
    private var normalColor = Color.BLACK

    init {

        context.obtainStyledAttributes(
            attrs,
            R.styleable.SegmentedControlView
        ).apply {

            binding.tvLeft.text =
                getString(R.styleable.SegmentedControlView_leftText)
                    ?: "Left"

            binding.tvRight.text =
                getString(R.styleable.SegmentedControlView_rightText)
                    ?: "Right"

            selectedIndex =
                getInt(
                    R.styleable.SegmentedControlView_selectedIndex,
                    0
                )

            selectedColor =
                getColor(
                    R.styleable.SegmentedControlView_selectedTextColor,
                    Color.RED
                )

            normalColor =
                getColor(
                    R.styleable.SegmentedControlView_normalTextColor,
                    Color.BLACK
                )

            binding.cardIndicator.setCardBackgroundColor(
                getColor(
                    R.styleable.SegmentedControlView_indicatorColor,
                    Color.WHITE
                )
            )

            binding.cardBackground.setCardBackgroundColor(
                getColor(
                    R.styleable.SegmentedControlView_segmentBackgroundColor,
                    Color.parseColor("#29A8AAAE")
                )
            )

            recycle()
        }

        binding.tvLeft.setOnClickListener {
            select(0)
        }

        binding.tvRight.setOnClickListener {
            select(1)
        }

        post {
            update(false)
        }
    }

    fun setSelected(index: Int) {
        selectedIndex = index
        update(false)
    }

    fun select(index: Int) {
        if (selectedIndex == index) return

        selectedIndex = index
        update(true)
        listener?.invoke(index)
    }

    fun getSelectedIndex(): Int = selectedIndex

    fun setOnTabSelectedListener(block: (Int) -> Unit) {
        listener = block
    }

    fun setTabs(left: String, right: String) {
        binding.tvLeft.text = left
        binding.tvRight.text = right
    }

    private fun update(animate: Boolean) {

        val margin = dp(4)

        val tabWidth = width / 2f

        val indicatorWidth = tabWidth - margin * 2

        binding.cardIndicator.layoutParams =
            binding.cardIndicator.layoutParams.apply {
                width = indicatorWidth.toInt()
            }

        binding.cardIndicator.requestLayout()

        val target =
            if (selectedIndex == 0)
                margin
            else
                tabWidth + margin

        if (animate) {
            binding.cardIndicator.animate()
                .translationX(target)
                .setDuration(180)
                .start()
        } else {
            binding.cardIndicator.translationX = target
        }

        binding.tvLeft.setTextColor(
            if (selectedIndex == 0)
                selectedColor
            else
                normalColor
        )

        binding.tvRight.setTextColor(
            if (selectedIndex == 1)
                selectedColor
            else
                normalColor
        )
    }

    private fun dp(value: Int): Float =
        value * resources.displayMetrics.density
}