package com.example.learnkotlin.domain.base.customview

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.text.InputFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.HandlerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.LayoutSearchInputBinding

class SearchInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // Khởi tạo binding chuẩn cho thẻ <merge> (chỉ truyền 2 tham số)
    private val binding: LayoutSearchInputBinding =
        LayoutSearchInputBinding.inflate(LayoutInflater.from(context), this)

    // Callback
    private var onFocusChangeCallback: ((Boolean) -> Unit)? = null
    private var onTextChangedCallback: ((String) -> Unit)? = null

    private var isProgrammaticChange = false

    init {
        setBackgroundResource(R.drawable.shape_bg_input_text_view)

        // Đảm bảo chiều cao tối thiểu cho ô bấm
        minHeight = context.resources.getDimensionPixelSize(R.dimen.dimen_48dp)

        // Đọc các thuộc tính từ XML truyền vào
        context.theme.obtainStyledAttributes(attrs, R.styleable.SearchInputView, 0, 0).apply {
            try {
                val hint = getString(R.styleable.SearchInputView_inputHint)
                val text = getString(R.styleable.SearchInputView_inputText)
                val iconRes = getResourceId(R.styleable.SearchInputView_leftIcon, -1)

                binding.etInput.hint = hint
                binding.etInput.setText(text)
                if (iconRes != -1) {
                    binding.ivLeft.setImageResource(iconRes)
                    binding.ivLeft.isVisible = true // Hiện icon nếu có dữ liệu truyền vào
                } else {
                    binding.ivLeft.isVisible = false // Ẩn hẳn đi nếu không truyền icon
                }

                // 2. Xử lý thuộc tính textSize (Mặc định nếu không truyền là -1)
                val textSize =
                    getDimensionPixelSize(R.styleable.SearchInputView_android_textSize, -1)
                if (textSize != -1) {
                    // Set dưới dạng PX vì getDimensionPixelSize đã tự quy đổi từ SP ra PX rồi
                    binding.etInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
                }

                // 3. Xử lý thuộc tính textColor
                val textColor = getColor(R.styleable.SearchInputView_android_textColor, -1)
                if (textColor != -1) {
                    binding.etInput.setTextColor(textColor)
                }

                // ---- XỬ LÝ MÀU HINT Ở ĐÂY ----
                val textColorHint = getColor(R.styleable.SearchInputView_android_textColorHint, -1)
                if (textColorHint != -1) {
                    binding.etInput.setHintTextColor(textColorHint)
                }

                // 4. Xử lý thuộc tính textStyle (Normal: 0, Bold: 1, Italic: 2)
                val textStyle =
                    getInt(R.styleable.SearchInputView_android_textStyle, Typeface.NORMAL)
                when (textStyle) {
                    1 -> binding.etInput.setTypeface(binding.etInput.typeface, Typeface.BOLD)
                    2 -> binding.etInput.setTypeface(binding.etInput.typeface, Typeface.ITALIC)
                    else -> binding.etInput.setTypeface(binding.etInput.typeface, Typeface.NORMAL)
                }
                // ---- XỬ LÝ MAXLENGTH Ở ĐÂY ----
                // Mặc định nếu không truyền thuộc tính này, giá trị nhận về sẽ là -1
                val maxLength = getInt(R.styleable.SearchInputView_android_maxLength, -1)
                if (maxLength >= 0) {
                    binding.etInput.filters = arrayOf(InputFilter.LengthFilter(maxLength))
                }
            } finally {
                recycle()
            }
        }

        // Focus callback
        binding.etInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateClearIcon()
            } else {
                postDelayed({
                    updateClearIcon()
                }, 50)
            }
            onFocusChangeCallback?.invoke(hasFocus)
        }

        // Text change callback
        binding.etInput.doOnTextChanged { text, _, _, _ ->
            updateClearIcon()
            if (!isProgrammaticChange) {
                onTextChangedCallback?.invoke(
                    text?.toString().orEmpty()
                )
            }
        }

        // Click vào nút X thì xóa hết text trong ô nhập
        binding.ivClear.setSafeOnClick {
            binding.etInput.text?.clear()
        }
    }

    fun updateClearIcon() {
        binding.ivClear.isVisible =
            binding.etInput.hasFocus() &&
                    binding.etInput.text?.isNotEmpty() == true
    }

    fun setOnFocusChangeListener(callback: (Boolean) -> Unit) {
        onFocusChangeCallback = callback
    }


    /**
     * Listen text change
     */
    fun setOnTextChangedListener(callback: (String) -> Unit) {
        onTextChangedCallback = callback
    }


    // Các hàm Helper để tương tác từ Activity/Fragment
    fun getText(): String = binding.etInput.text.toString()

    fun setText(
        text: String,
        notifyTextChanged: Boolean = true
    ) {
        if (binding.etInput.text.toString() == text) {
            return
        }
        val oldState = isProgrammaticChange
        isProgrammaticChange = !notifyTextChanged
        try {
            binding.etInput.setText(text)
            binding.etInput.setSelection(binding.etInput.text.length)
        } finally {
            isProgrammaticChange = oldState
        }
    }

    fun getHint(): String = binding.etInput.hint.toString()

    fun setHint(text: String) {
        binding.etInput.hint = text
    }

    fun setHintTextColor(color: Int) {
        binding.etInput.setHintTextColor(color)
    }

    fun setMaxLength(length: Int) {
        if (length >= 0) {
            binding.etInput.filters = arrayOf(InputFilter.LengthFilter(length))
        } else {
            // Nếu truyền số âm thì xóa bỏ bộ lọc giới hạn ký tự đi
            binding.etInput.filters = arrayOf()
        }
    }

    fun setEllipsize(enable: Boolean) {
        if (enable) {
            binding.etInput.apply {
                isSingleLine = true
                ellipsize = android.text.TextUtils.TruncateAt.END
                maxLines = 1
            }
        } else {
            binding.etInput.ellipsize = null
        }
    }

    fun getEditText() = binding.etInput
}