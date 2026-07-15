package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnkotlin.core.extensions.dpToPx
import com.example.learnkotlin.core.extensions.hideKeyboard
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.DialogFeedbackBinding
import com.example.learnkotlin.domain.model.home.TypeFeedback
import com.example.learnkotlin.presentation.base.adapter.GridSpacingItemDecoration
import com.example.learnkotlin.presentation.base.dialog.BaseDialogFragment

class FeedbackDialog() : BaseDialogFragment<DialogFeedbackBinding>() {

    var onConfirmNo: (() -> Unit)? = null
    var onConfirmYes: (() -> Unit)? = null

    val typeFeedbackAdapter: TypeFeedbackAdapter by lazy {
        TypeFeedbackAdapter()
    }

    private fun setupRecyclerView() {
        binding.rvApplyCamera.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(2, 12.dpToPx(requireContext()), true))
            adapter = typeFeedbackAdapter
            typeFeedbackAdapter.setSelectionMode(SelectionMode.SINGLE)
            typeFeedbackAdapter.submitList(getTypeFeedbackList())
        }
    }

    private fun getTypeFeedbackList(): List<TypeFeedback> {
        return listOf(
            TypeFeedback("Complaint"),
            TypeFeedback("Suggestion", isSelected = true),
            TypeFeedback("Lost item"),
            TypeFeedback("Other"),
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFeedbackBinding {
        return DialogFeedbackBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        updateSubmitButtonState()
        setupRecyclerView()
    }

    override fun setupListener() {
        // Ẩn bàn phím khi nhấn vào vùng trống của dialog
        binding.root.setOnClickListener {
            it.hideKeyboard()
        }

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
        binding.edtShortTitle.doAfterTextChanged {
            updateSubmitButtonState()
        }

        binding.metDescription.doAfterTextChanged {
            updateSubmitButtonState()
        }

    }

    fun View.setEnabledWithAlpha(enabled: Boolean) {
        isEnabled = enabled
        alpha = if (enabled) 1f else 0.65f
    }

    private fun updateSubmitButtonState() {
        val hasSubject = binding.edtShortTitle.text?.toString()?.trim().orEmpty().isNotEmpty()
        val hasDescription = binding.metDescription.text?.toString()?.trim().orEmpty().isNotEmpty()
        binding.btnConfirmYes.setEnabledWithAlpha(hasSubject && hasDescription)
    }

    override val dialogWidth: Int
        get() = (resources.displayMetrics.widthPixels * 0.85f).toInt()

}
