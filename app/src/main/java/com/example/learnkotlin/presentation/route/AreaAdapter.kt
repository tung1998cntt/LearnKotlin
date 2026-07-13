package com.example.learnkotlin.presentation.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ItemAreaBinding
import com.example.learnkotlin.domain.model.home.Area

class AreaAdapter(
    private val onClick: (Area) -> Unit
) : ListAdapter<Area, AreaAdapter.AreaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaViewHolder {

        val binding = ItemAreaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AreaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AreaViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class AreaViewHolder(
        private val binding: ItemAreaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Area) {

            binding.tvArea.text = item.name

            binding.root.isSelected = item.isSelected

            binding.root.setSafeOnClick {
                onClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Area>() {

        override fun areItemsTheSame(
            oldItem: Area,
            newItem: Area
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Area,
            newItem: Area
        ): Boolean {
            return oldItem == newItem
        }
    }
}