package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.DialogProfileBinding
import com.example.learnkotlin.presentation.base.dialog.BaseDialogFragment

class ProfileDialog() : BaseDialogFragment<DialogProfileBinding>() {

     var onConfirmNo: (() -> Unit)? = null
     var onConfirmYes: (() -> Unit)? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogProfileBinding {
        return DialogProfileBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
       /* To do*/
    }

    override fun setupListener() {
        binding.btnConfirmNo.setSafeOnClick {
            dismiss()
            onConfirmNo?.invoke()
        }

        binding.btnConfirmYes.setSafeOnClick {
            dismiss()
            onConfirmYes?.invoke()
        }

        binding.ivClose.setSafeOnClick {
            dismiss()
        }
    }

    override val dialogWidth: Int
        get() = (resources.displayMetrics.widthPixels * 0.85f).toInt()

}