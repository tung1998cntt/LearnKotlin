package com.example.learnkotlin.presentation.route

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.BuildConfig
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ItemRouteDirectionBinding
import com.example.learnkotlin.databinding.ItemRouteInformationBinding
import com.example.learnkotlin.databinding.ItemRouteMapBinding
import com.example.learnkotlin.databinding.ItemRouteSegmentBinding
import com.example.learnkotlin.databinding.ItemRouteStopBinding
import com.example.learnkotlin.databinding.ItemRouteSummaryBinding
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.RouteStop
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.presentation.base.customview.VerticalDashDrawable

class RouteDetailAdapter(
    private val listener: Listener
) : ListAdapter<RouteDetailItem, RecyclerView.ViewHolder>(DiffCallback()) {


    private var mapHolder: MapViewHolder? = null

    interface Listener {

        fun onOutboundClick()

        fun onInboundClick()

        fun onRouteInformationClick()

        fun onBusStopClick()

        fun onStopClick(stop: RouteStop)

    }

    companion object {

        private const val TYPE_SUMMARY = 0

        private const val TYPE_DIRECTION = 1

        private const val TYPE_MAP = 2

        private const val TYPE_SEGMENT = 3

        private const val TYPE_INFORMATION = 4

        private const val TYPE_STOP = 5

    }

    fun onStart() {
        mapHolder?.onStart()
    }

    fun onResume() {
        mapHolder?.onResume()
    }

    fun onPause() {
        mapHolder?.onPause()
    }

    fun onStop() {
        mapHolder?.onStop()
    }

    fun onDestroy() {
        mapHolder?.onDestroy()
    }

    fun onLowMemory() {
        mapHolder?.onLowMemory()
    }

    override fun getItemViewType(position: Int): Int {

        return when (getItem(position)) {

            is RouteDetailItem.Summary -> TYPE_SUMMARY

            is RouteDetailItem.Direction -> TYPE_DIRECTION

            is RouteDetailItem.Map -> TYPE_MAP

            is RouteDetailItem.Segment -> TYPE_SEGMENT

            is RouteDetailItem.Information -> TYPE_INFORMATION

            is RouteDetailItem.Stop -> TYPE_STOP

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {

            TYPE_SUMMARY ->

                SummaryViewHolder(
                    ItemRouteSummaryBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

            TYPE_DIRECTION ->

                DirectionViewHolder(
                    ItemRouteDirectionBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    listener
                )

            TYPE_MAP ->

                MapViewHolder(
                    ItemRouteMapBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                ).also {

                    mapHolder = it

                }

            TYPE_SEGMENT ->

                SegmentViewHolder(
                    ItemRouteSegmentBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    listener
                )

            TYPE_INFORMATION ->

                InformationViewHolder(
                    ItemRouteInformationBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

            else ->

                StopViewHolder(
                    ItemRouteStopBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    listener
                )

        }

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (val item = getItem(position)) {

            is RouteDetailItem.Summary ->

                (holder as SummaryViewHolder).bind(item)

            is RouteDetailItem.Direction ->

                (holder as DirectionViewHolder).bind(item)

            is RouteDetailItem.Map ->

                (holder as MapViewHolder).bind(item)

            is RouteDetailItem.Segment ->

                (holder as SegmentViewHolder).bind(item)

            is RouteDetailItem.Information ->

                (holder as InformationViewHolder).bind(item)

            is RouteDetailItem.Stop ->

                (holder as StopViewHolder).bind(item)

        }

    }


    class SummaryViewHolder(
        private val binding: ItemRouteSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteDetailItem.Summary) {

            binding.tvFare.text = item.fare

            binding.tvDistance.text = item.distance

            binding.tvStops.text = item.stopCount.toString()
        }
    }

    class DirectionViewHolder(
        private val binding: ItemRouteDirectionBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteDetailItem.Direction) {

            binding.segment.setSelected(
                if (item.selected == Variant.OUTBOUND) 0 else 1
            )

            binding.segment.setOnTabSelectedListener {
                if (it == 0)
                    listener.onOutboundClick()
                else
                    listener.onInboundClick()
            }
        }
    }


    class MapViewHolder(
        val binding: ItemRouteMapBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var mapCreated = false

        fun bind(item: RouteDetailItem.Map) {

            if (!mapCreated) {

                mapCreated = true

                binding.mapView.onCreate(null)

                binding.mapView.getMapAsync { map ->
                    val styleUrl =
                        "https://api.maptiler.com/maps/streets-v2/style.json?key=${BuildConfig.MAPTILER_API_KEY}"
                    map.setStyle(styleUrl)
                    // draw route
                    // draw marker

                }
            }
        }

        fun onStart() = binding.mapView.onStart()

        fun onResume() = binding.mapView.onResume()

        fun onPause() = binding.mapView.onPause()

        fun onStop() = binding.mapView.onStop()

        fun onLowMemory() = binding.mapView.onLowMemory()

        fun onDestroy() = binding.mapView.onDestroy()
    }

    class SegmentViewHolder(
        private val binding: ItemRouteSegmentBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteDetailItem.Segment) {

            val routeSelected =
                item.selected == SegmentType.ROUTE_INFORMATION

            binding.tvRouteInformation.isSelected = routeSelected
            binding.tvBusStop.isSelected = !routeSelected

            binding.tvRouteInformation.setOnClickListener {
                if (!routeSelected) {
                    listener.onRouteInformationClick()
                }
            }

            binding.tvBusStop.setOnClickListener {
                if (routeSelected) {
                    listener.onBusStopClick()
                }
            }
        }
    }

    class InformationViewHolder(
        private val binding: ItemRouteInformationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteDetailItem.Information) {

            binding.tvOperator.text =
                item.operator

            binding.tvPayment.text =
                item.payment

            binding.tvDay1.text =
                item.operatingHours.day1

            binding.tvDay2.text =
                item.operatingHours.day2

            binding.tvDay3.text =
                item.operatingHours.day3

            binding.tvTime1.text =
                item.operatingHours.time1

            binding.tvTime2.text =
                item.operatingHours.time2

            binding.tvTime3.text =
                item.operatingHours.time3
        }
    }


    class StopViewHolder(
        private val binding: ItemRouteStopBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dashDrawable by lazy {
            VerticalDashDrawable(
                color = Color.parseColor("#C8C7CC"),
                dashLength = dp(4).toFloat(),
                dashGap = dp(3).toFloat(),
                strokeWidth = dp(2).toFloat()
            )
        }
        init {
            binding.viewTop.background = dashDrawable
            binding.viewBottom.background = dashDrawable
        }
        fun bind(item: RouteDetailItem.Stop) {

            binding.tvName.text = item.stop.stopName

            binding.tvDistance.text = item.distanceText ?: ""

            binding.viewTop.visibility =
                if (item.isFirst) View.INVISIBLE else View.VISIBLE

            binding.viewBottom.visibility =
                if (item.isLast) View.INVISIBLE else View.VISIBLE

            val lp = binding.imgPoint.layoutParams
            when {
                item.isFirst -> {
                    binding.imgPoint.setImageResource(R.drawable.ic_blue_point_27)
                    lp.width = dp(27)
                    lp.height = dp(27)
                }

                item.isLast -> {
                    binding.imgPoint.setImageResource(R.drawable.ic_marker_selected)
                    lp.width = dp(49)
                    lp.height = dp(53)
                }

                else -> {
                    binding.imgPoint.setImageResource(R.drawable.ic_stop_gray)
                    lp.width = dp(27)
                    lp.height = dp(27)
                }
            }

            binding.imgPoint.layoutParams = lp
            binding.root.setSafeOnClick {
                listener.onStopClick(item.stop)
            }
        }

        private fun dp(value: Int): Int {
            return (value * binding.root.resources.displayMetrics.density).toInt()
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<RouteDetailItem>() {

        override fun areItemsTheSame(
            oldItem: RouteDetailItem,
            newItem: RouteDetailItem
        ): Boolean {

            return when {

                oldItem is RouteDetailItem.Summary &&
                        newItem is RouteDetailItem.Summary ->
                    true

                oldItem is RouteDetailItem.Direction &&
                        newItem is RouteDetailItem.Direction ->
                    true

                oldItem is RouteDetailItem.Map &&
                        newItem is RouteDetailItem.Map ->
                    true

                oldItem is RouteDetailItem.Segment &&
                        newItem is RouteDetailItem.Segment ->
                    true

                oldItem is RouteDetailItem.Information &&
                        newItem is RouteDetailItem.Information ->
                    true

                oldItem is RouteDetailItem.Stop &&
                        newItem is RouteDetailItem.Stop ->
                    oldItem.stop.id == newItem.stop.id

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: RouteDetailItem,
            newItem: RouteDetailItem
        ): Boolean {

            return oldItem == newItem

        }

    }

}