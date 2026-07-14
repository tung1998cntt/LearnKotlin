package com.example.learnkotlin.presentation.base.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.learnkotlin.R
import com.example.learnkotlin.databinding.DialogConfirmBinding
class ConfirmDialog(
    private val title: String? = null,
    private val message: CharSequence? = null,
    private val onConfirm: (() -> Unit)? = null,
    private val onCancel: (() -> Unit)? = null,
    private val showTitle: Boolean = true,
    private val showMessage: Boolean = true,
    private val showCancelButton: Boolean = true,
    private val showConfirmButton: Boolean = true,
    private var confirmText: String? = null,
    private var cancelText: String? = null,
    private val cancelOnTouchOutside: Boolean = true,
    private val icon: Int? = null,
    private val showIcon: Boolean = false,
) : BaseDialogFragment<DialogConfirmBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmBinding {
        return DialogConfirmBinding.inflate(inflater, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(cancelOnTouchOutside)
        isCancelable = cancelOnTouchOutside
    }

    override fun setupView() {

        binding.icStatusDialog.apply {
            if (icon != null) {
                setImageResource(icon)
            }
            isVisible = showIcon && icon != null
        }
        binding.tvTitle.apply {
            text = title
            isVisible = showTitle && !title.isNullOrEmpty()
        }
        binding.tvMessage.apply {
            text = message
            isVisible = showMessage && !message.isNullOrEmpty()
        }
        binding.btnConfirmYes.apply {
            isVisible = showConfirmButton
            text = confirmText ?: requireContext().getString(R.string.continue_title)
        }
        binding.btnConfirmNo.apply {
            isVisible = showCancelButton
            text = cancelText ?: requireContext().getString(R.string.cancel_title)
        }
    }

    override fun setupListener() {
        binding.btnConfirmYes.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
        binding.btnConfirmNo.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }
    }

    override val dialogWidth: Int
        get() = (resources.displayMetrics.widthPixels * 0.85f).toInt()

}
