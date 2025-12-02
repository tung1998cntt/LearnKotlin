package com.example.learnkotlin.presentation.base.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.learnkotlin.R

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    // --- MUST Implement ---
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun setupView()
    open fun setupObserver() {}
    open fun setupListener() {}

    // --- Config options ---
    open val dialogWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    open val dialogHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    open val dialogCancelable: Boolean = true
    open val dialogAnimation: Int? = null     // e.g. R.style.DialogFadeAnimation
    open val isFullScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = dialogCancelable

        if (isFullScreen) {
            setStyle(STYLE_NORMAL, R.style.BaseDialogFullScreen)
        } else {
            setStyle(STYLE_NORMAL, R.style.BaseDialogNormal)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(dialogWidth, dialogHeight)
            dialogAnimation?.let { setWindowAnimations(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
        setupObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
