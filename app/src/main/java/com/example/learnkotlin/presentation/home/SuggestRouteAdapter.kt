package com.example.learnkotlin.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.databinding.ItemSuggestRouteBinding

class SuggestRouteAdapter(
    private val listener: Listener
) : ListAdapter<SuggestRouteItem, SuggestRouteAdapter.ViewHolder>(Diff()) {

    interface Listener {
        fun onRouteClick(item: SuggestRouteItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemSuggestRouteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemSuggestRouteBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestRouteItem) {

            binding.tvRouteName.text = item.routeName

            binding.tvBusTime.text =
                "${item.busMinutes} min"

            binding.tvWalkTime.text =
                "${item.walkMinutes} min walk"

            binding.root.setOnClickListener {
                listener.onRouteClick(item)
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<SuggestRouteItem>() {

        override fun areItemsTheSame(
            oldItem: SuggestRouteItem,
            newItem: SuggestRouteItem
        ) = oldItem.routeId == newItem.routeId

        override fun areContentsTheSame(
            oldItem: SuggestRouteItem,
            newItem: SuggestRouteItem
        ) = oldItem == newItem
    }
}