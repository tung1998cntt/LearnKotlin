package com.example.learnkotlin.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.databinding.ItemNearbyArrivalBinding

class NearbyArrivalAdapter(
    private val listener: Listener
) : ListAdapter<NearbyArrivalItem, NearbyArrivalAdapter.ViewHolder>(Diff()) {

    interface Listener {
        fun onArrivalClick(item: NearbyArrivalItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemNearbyArrivalBinding.inflate(
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
        private val binding: ItemNearbyArrivalBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NearbyArrivalItem) {

            binding.tvRouteName.text = item.routeName

            binding.tvPlate.text =
                "Plate ${item.plate}"

            binding.tvTime.text =
                "ETA ${item.etaTime}"

            binding.tvTotalTime.text =
                "${item.etaMinutes} min"

            binding.root.setOnClickListener {
                listener.onArrivalClick(item)
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<NearbyArrivalItem>() {

        override fun areItemsTheSame(
            oldItem: NearbyArrivalItem,
            newItem: NearbyArrivalItem
        ) = oldItem.routeId == newItem.routeId &&
                oldItem.plate == newItem.plate

        override fun areContentsTheSame(
            oldItem: NearbyArrivalItem,
            newItem: NearbyArrivalItem
        ) = oldItem == newItem
    }
}