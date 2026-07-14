package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ItemTypeFeedbackBinding
import com.example.learnkotlin.domain.model.home.TypeFeedback

class TypeFeedbackAdapter :
    RecyclerView.Adapter<TypeFeedbackAdapter.ViewHolder>() {

    private val items = mutableListOf<TypeFeedback>()

    private var selectionMode: SelectionMode = SelectionMode.MULTIPLE
    var onItemSelected: ((TypeFeedback) -> Unit)? = null

    inner class ViewHolder(
        val binding: ItemTypeFeedbackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setSafeOnClick {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    if (!item.isEnabled) return@setSafeOnClick
                    when (selectionMode) {
                        SelectionMode.MULTIPLE -> {
                            item.isSelected = !item.isSelected
                            notifyItemChanged(position)
                        }

                        SelectionMode.SINGLE -> {
                            if (item.isSelected) return@setSafeOnClick
                            val prevIndex = items.indexOfFirst { it.isSelected }
                            if (prevIndex != -1) {
                                items[prevIndex].isSelected = false
                                notifyItemChanged(prevIndex)
                            }

                            item.isSelected = true
                            notifyItemChanged(position)
                            onItemSelected?.invoke(item)
                        }
                    }
                }
            }
        }

        fun bind(item: TypeFeedback) {
            binding.tvApplyCamera.text = item.name
            itemView.isEnabled = item.isEnabled
            itemView.isSelected = item.isSelected
            if (item.isEnabled) {
                binding.tvApplyCamera.alpha = 1f
                if (item.isSelected) {
                    binding.tvApplyCamera.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.bg_back)
                } else {
                    binding.tvApplyCamera.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.shape_schedule_take_photo_normal)
                }
            } else {
                binding.tvApplyCamera.alpha = 0.65f
//                if (item.isSelected) {
//                    binding.tvApplyCamera.background =
//                        ContextCompat.getDrawable(binding.root.context, R.drawable.bg_status_3)
//                    binding.tvApplyCamera.setTextColor(
//                        ContextCompat.getColor(
//                            binding.root.context,
//                            R.color.color_FF3B30
//                        )
//                    )
//                } else {
//                    binding.tvApplyCamera.background =
//                        ContextCompat.getDrawable(binding.root.context, R.drawable.bg_status_2)
//                    binding.tvApplyCamera.setTextColor(
//                        ContextCompat.getColor(
//                            binding.root.context,
//                            R.color.color_4B4B4B
//                        )
//                    )
//                }
            }
        }
    }

    fun setSelectionMode(mode: SelectionMode) {
        if (selectionMode == mode) return
        selectionMode = mode
        if (mode == SelectionMode.SINGLE) {
            val firstSelectedIndex = items.indexOfFirst { it.isSelected }
            items.forEachIndexed { index, item ->
                item.isSelected = index == firstSelectedIndex
            }
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTypeFeedbackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(data: List<TypeFeedback>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun getSelected(): List<TypeFeedback> =
        items.filter { it.isSelected }

    fun getBitmask(): Int {
        var mask = 0
        items.forEachIndexed { index, item ->
            if (item.isSelected) {
                mask = mask or (1 shl index)
            }
        }
        return mask
    }

    fun getIndexSelected() = items.indexOfFirst { it.isSelected }

    fun getListItem() = items

}

enum class SelectionMode {
    SINGLE,
    MULTIPLE
}
