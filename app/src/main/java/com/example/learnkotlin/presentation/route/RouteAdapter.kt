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
            binding.tvStops.text =  binding.root.context.getString(R.string.stops_number, getFakeStops(item.routeName))
            binding.tvStartEnd.text = getFakeRouteName(item.routeName)
            binding.tvDetail.setSafeOnClick {
                onClick(item)
            }
        }

        private fun getFakeStops(name: String?): String {
            return when (name) {
                "TAMKAS" -> "24"
                "Pontbuiten" -> "17"
                "PG" -> "14"
                "Lijn_10" -> "13"
                "Lijn_9" -> "12"
                "Lijn_8" -> "11"
                "Lijn_7" -> "9"
                "Lijn_6" -> "12"
                "Lijn_5" -> "10"
                "Lijn_4" -> "10"
                "Lijn_2" -> "13"
                "Lijn_1" -> "12"
                "TUNG1998" -> "3"
                else -> "8"
            }
        }


        private fun getFakeRouteName(name: String?): String {
            return when (name) {
                "TAMKAS" -> "Beginhalte A.L. Waaldijkstaat - troelistraat links"
                "Pontbuiten" -> "Beginhalte C.H.M Dr. Sophie Redmondstraat links - Rambali Mathoeraweg links"
                "PG" -> "Beginhalte Waterkant Plattebrug - Bonistraat rechts"
                "Lijn_10" -> "Beginhalte Heiligenweg - Jozef Israelsstraat rechts"
                "Lijn_9" -> "Beginhalte Steenbakkerijstraat - Androestraat rechts"
                "Lijn_8" -> "Beginhalte Steenbakkerijstraat - Licaniastraat"
                "Lijn_7" -> "Beginhalte C.H.M. Dr. Sophie Redmondstraat - Indira Gandhiweg links"
                "Lijn_6" -> "Beginhalte C.H.M. - Reineweg links"
                "Lijn_5" -> "Beginhalte Waterkant (S.M.S. Veersteiger) rechts - Ringweg links"
                "Lijn_4" -> "Beginhalte Waterkant (Platte Brug) - Anton Dragtenweg - Morgenstondlaan"
                "Lijn_2" -> "Beginhalte Saramaccastraat - Poelepantje links"
                "Lijn_1" -> "Beginhalte C.H.M. - Dr. Sophie Redmondstraat - Brokopondelaan links"
                "TUNG1998" -> "55 Nguyễn Viết Xuân, Hà Đông, Hà Nội - Vật lý trị liệu tại nhà, Hà Đông, Hà Nội"
                else -> "Beginhalte A.L. Waaldijkstaat - troelistraat links"
            }
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