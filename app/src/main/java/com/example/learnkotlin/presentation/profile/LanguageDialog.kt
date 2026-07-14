package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.DialogLanguageBinding
import com.example.learnkotlin.presentation.base.dialog.BaseDialogFragment

class LanguageDialog() : BaseDialogFragment<DialogLanguageBinding>() {

     var onEnglish: (() -> Unit)? = null
     var onNederlands: (() -> Unit)? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogLanguageBinding {
        return DialogLanguageBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
       /* To do*/
    }

    override fun setupListener() {
        binding.tvEnglish.setSafeOnClick {
            dismiss()
            onEnglish?.invoke()
        }

        binding.tvNederlands.setSafeOnClick {
            dismiss()
            onNederlands?.invoke()
        }

        binding.ivClose.setSafeOnClick {
            dismiss()
        }
    }

    override val dialogWidth: Int
        get() = (resources.displayMetrics.widthPixels * 0.85f).toInt()

}