package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnkotlin.core.extensions.dpToPx
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
        setupRecyclerView()
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