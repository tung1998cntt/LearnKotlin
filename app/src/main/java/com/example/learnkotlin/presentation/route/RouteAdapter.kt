package com.example.learnkotlin.presentation.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ItemRouteBinding
import com.example.learnkotlin.domain.model.home.RouteItem

class RouteAdapter(
    private val onClick: (RouteItem) -> Unit
) : ListAdapter<RouteItem, RouteAdapter.RouteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteViewHolder {

        val binding = ItemRouteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RouteViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RouteViewHolder(
        private val binding: ItemRouteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteItem) {

            binding.tvRouteName.text = item.routeName
            binding.tvPrice.text = "SRD 8"
            binding.tvDistance.text = binding.root.context.getString(R.string.km_value, item.outboundDistance)
            binding.tvFrequency.text = binding.root.context.getString(R.string.every_s_min, "15")
            binding.tvStops.text =  binding.root.context.getString(R.string.stops_number, getFakeStops(bindingAdapterPosition))
            binding.tvStartEnd.text = "CHM Building - Hermitageweg"
            binding.tvDetail.setSafeOnClick {
                onClick(item)
            }
        }

        private fun getFakeStops(position: Int): String {
            val stops = listOf(24, 17, 14, 13, 12, 11, 9, 12, 10, 10, 13, 12, 3)
            val count = stops.getOrElse(position) { 8 }
            return "$count"
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<RouteItem>() {

        override fun areItemsTheSame(
            oldItem: RouteItem,
            newItem: RouteItem
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: RouteItem,
            newItem: RouteItem
        ) = oldItem == newItem
    }
}