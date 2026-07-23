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
            binding.tvStartEnd.text = getFakeRouteName(bindingAdapterPosition)
            binding.tvDetail.setSafeOnClick {
                onClick(item)
            }
        }

        private fun getFakeStops(position: Int): String {
            val stops = listOf(24, 17, 14, 13, 12, 11, 9, 12, 10, 10, 13, 12, 3)
            val count = stops.getOrElse(position) { 8 }
            return "$count"
        }

        private fun getFakeRouteName(position: Int): String {
            val stops = listOf("Beginhalte A.L. Waaldijkstaat - troelistraat links",
                "Beginhalte C.H.M Dr. Sophie Redmondstraat links - Rambali Mathoeraweg links",
                "Beginhalte Waterkant Plattebrug - Bonistraat rechts",
                "Beginhalte Heiligenweg - Jozef Israelsstraat rechts",
                "Beginhalte Steenbakkerijstraat - Androestraat rechts",
                "Beginhalte Steenbakkerijstraat - Licaniastraat",
                "Beginhalte C.H.M. Dr. Sophie Redmondstraat - Indira Gandhiweg links",
                "Beginhalte C.H.M. - Reineweg links",
                "Beginhalte Waterkant (S.M.S. Veersteiger) rechts - Ringweg links",
                "Beginhalte Waterkant (Platte Brug) - Anton Dragtenweg - Morgenstondlaan",
                "Beginhalte Saramaccastraat - Poelepantje links",
                "Beginhalte C.H.M. - Dr. Sophie Redmondstraat - Brokopondelaan links",
                "55 Nguyễn Viết Xuân, Hà Đông, Hà Nội - Vật lý trị liệu tại nhà, Hà Đông, Hà Nội")
            val count = stops.getOrElse(position) { "Beginhalte A.L. Waaldijkstaat - troelistraat links" }
            return count
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