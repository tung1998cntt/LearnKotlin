package com.example.learnkotlin.presentation.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.FragmentRouteBinding
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.RouteItem
import com.example.learnkotlin.domain.model.home.RouteStop
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.home.HomeCommand
import com.example.learnkotlin.presentation.home.HomeEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : BaseFragment<FragmentRouteBinding>() {

    override val viewModel: RouteViewModel by viewModels()
    private val adapter by lazy {
        RouteAdapter { route ->
            sendCommand(
                HomeCommand.GetRouteDetail(
                    route.id
                )
            )
        }
    }

    private val areaAdapter by lazy {
        AreaAdapter {
            sendCommand(
                HomeCommand.SelectArea(it)
            )
        }
    }

    private val detailAdapter by lazy {

        RouteDetailAdapter(

            object : RouteDetailAdapter.Listener {

                override fun onOutboundClick() {

                    sendCommand(

                        HomeCommand.ChangeVariant(
                            Variant.OUTBOUND
                        )
                    )
                }

                override fun onInboundClick() {

                    sendCommand(

                        HomeCommand.ChangeVariant(
                            Variant.INBOUND
                        )
                    )
                }

                override fun onRouteInformationClick() {

                    sendCommand(

                        HomeCommand.ChangeSegment(
                            SegmentType.ROUTE_INFORMATION
                        )
                    )
                }

                override fun onBusStopClick() {

                    sendCommand(

                        HomeCommand.ChangeSegment(
                            SegmentType.BUS_STOP
                        )
                    )
                }

                override fun onStopClick(stop: RouteStop) {

                }
            }
        )
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRouteBinding {
        return FragmentRouteBinding.inflate(inflater, container, false)
    }

    override fun onInit() {
        initBottomSheet()
        initRecyclerView()
        sendCommand(HomeCommand.GetArea)
        sendCommand(HomeCommand.GetRoute)
        observeState()
        initAction()

    }

    private fun initAction() {
        binding.sbSearchRoute.setOnTextChangedListener {text ->
            sendCommand(
                HomeCommand.SearchRoute(
                    text
                )
            )
        }
        binding.imBack.setSafeOnClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.imgClose.setSafeOnClick {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.viewScrim.visibility = View.VISIBLE
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.viewScrim.visibility = View.GONE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.viewScrim.alpha = slideOffset.coerceIn(0f, 1f)
                }
            }
        )

        binding.viewScrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun observeState() {
        collectState<RouteState, List<RouteItem>>(
            selector = { it.routes }
        ) {
            adapter.submitList(it)
        }

        collectState<RouteState, List<Area>>(
            selector = { it.areas }
        ) {
            areaAdapter.submitList(it)
        }
        collectState<RouteState, List<RouteDetailItem>>(
            selector = {
                it.detailItems
            }
        ) {
            detailAdapter.submitList(it)
        }
    }

    private fun initBottomSheet() {

        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet)

        val height = (resources.displayMetrics.heightPixels * 0.85f).toInt()

        binding.layoutBottomSheet.layoutParams.height = height

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.isHideable = true
        binding.layoutBottomSheet.post {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initRecyclerView() {
        binding.rvRoutes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RouteFragment.adapter
        }

        binding.rvArea.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = areaAdapter
        }
        binding.rvRouteDetail.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = detailAdapter
        }
        addLoadMore()
    }

    private fun addLoadMore() {

        binding.rvRoutes.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    if (dy <= 0) return
                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    val lastVisible =
                        layoutManager.findLastVisibleItemPosition()
                    val total =
                        layoutManager.itemCount
                    val state = viewModel.state.value
                    if (!state.loadingMore &&
                        state.hasNext &&
                        lastVisible >= total - 3
                    ) {
                        sendCommand(
                            HomeCommand.LoadMore
                        )

                    }
                }
            }
        )
    }



    override fun handleEvent(event: Any) {
        when (event) {

            is UiEvent.Error -> {
                showErrorDialog(event.message, onConfirm = {

                })
            }

            is HomeEvent.OpenRouteDetail -> {
                val detail =
                    viewModel.state.value.routeDetail ?: return
                binding.tvRouteName.text =
                    detail.routeName
                binding.tvFrequency.text =
                    "Every 15 min"
                binding.tvDestination.text =
                    if (viewModel.state.value.variant == Variant.OUTBOUND)
                        detail.outboundStops?.lastOrNull()?.stopName
                    else
                        detail.inboundStops?.lastOrNull()?.stopName
                bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED
            }

        }
    }

    override fun onStart() {
        super.onStart()
        detailAdapter.onStart()
    }

    override fun onResume() {
        super.onResume()
        detailAdapter.onResume()
    }

    override fun onPause() {
        detailAdapter.onPause()
        super.onPause()
    }

    override fun onStop() {
        detailAdapter.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        detailAdapter.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        detailAdapter.onLowMemory()
    }
}
