package com.example.learnkotlin.presentation.base.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.learnkotlin.databinding.DialogConfirmBinding

class ConfirmDialog(
    private val title: String? = null,
    private val message: String? = null,
    private val onConfirm: (() -> Unit)? = null
) : BaseDialogFragment<DialogConfirmBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmBinding {
        return DialogConfirmBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.tvTitle.text = title
        binding.tvMessage.text = message
    }

    override fun setupListener() {
        binding.btnConfirmYes.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
        binding.btnConfirmNo.setOnClickListener { dismiss() }
    }

    override val dialogWidth: Int
        get() = (resources.displayMetrics.widthPixels * 0.85f).toInt()

}
