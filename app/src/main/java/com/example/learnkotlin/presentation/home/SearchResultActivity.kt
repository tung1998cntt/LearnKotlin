package com.example.learnkotlin.presentation.home

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ActivitySearchResultBinding
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.domain.base.customview.SearchInputView
import com.example.learnkotlin.domain.model.home.LocationSearch
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.RouteStop
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.base.baseDropdown.BaseDropdownAdapter
import com.example.learnkotlin.presentation.route.RouteDetailAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>() {

    override val viewModel: SearchResultModel by viewModels()

    private lateinit var popupCurrentLocation: ListPopupWindow
    private lateinit var adapterCurrentLocation: BaseDropdownAdapter<SearchLocation>

    private lateinit var popupDestination: ListPopupWindow
    private lateinit var adapterDestination: BaseDropdownAdapter<SearchLocation>

    private val adapter = SuggestRouteAdapter(object : SuggestRouteAdapter.Listener {

        override fun onRouteClick(item: SuggestRouteItem) {
            viewModel.suggestData = item
            sendCommand(
                HomeCommand.GetRouteDetail(
                    item.routeId
                )
            )
        }
    })

    private val adapterArrival = NearbyArrivalAdapter(
        object : NearbyArrivalAdapter.Listener {

            override fun onArrivalClick(item: NearbyArrivalItem) {
                viewModel.nearbyArrivalData = item
                sendCommand(HomeCommand.GetRouteDetail(item.routeId))
            }
        }
    )

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
                    if (::bottomSheetBehavior.isInitialized) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    val resultData = SearchLocation(
                        name = stop.stopName,
                        latitude = stop.latitude,
                        longitude = stop.longitude
                    )
                    val resultIntent = Intent().apply {
                        putExtra("data", resultData)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        )
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun inflateBinding(): ActivitySearchResultBinding =
        ActivitySearchResultBinding.inflate(layoutInflater)

    override fun onInit() {
        initBottomSheet()
        val locationSearch = navData as? LocationSearch
        // Cập nhật dữ liệu từ navData vào ViewModel trước
        locationSearch?.let {
            sendCommand(HomeCommand.SelectCurrentLocation(it.selectedCurrentLocation?.name ?: "", it.selectedCurrentLocation))
            sendCommand(HomeCommand.SelectDestination(it.selectedDestination?.name ?: "", it.selectedDestination))
        }
        initView()
        initPopup()
        initAction()
        observeState()
        sendCommand(HomeCommand.GetSuggestRoutes(navData as? LocationSearch))
    }

    private fun setArrivingYourStop(stopName: String) {
        val text = getString(R.string.arriving_at_your_stop_s, stopName)
        val spannable = SpannableString(text)
        val start = text.indexOf(stopName)
        if (start != -1) {
            val end = start + stopName.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvArrivingYourStop.text = spannable
    }
    private fun initView() {
        displayViewWithTab()
        binding.lnFindRoute.setEnabledWithAlpha(
            !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
            !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
        )
        binding.rvSuggestedRoutes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchResultActivity.adapter
        }

        binding.rvArrivingBuses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchResultActivity.adapterArrival
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition() ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0

                    if (lastVisibleItemPosition >= totalItemCount - 5) {
                        sendCommand(HomeCommand.LoadMore)
                    }
                }
            })
        }

        binding.rvRouteDetail.apply {
            layoutManager =
                LinearLayoutManager(this@SearchResultActivity)
            val flow =
                if (viewModel.tabRoute == TabRoute.SUGGEST) FlowRoute.SUGGEST_ROUTE else FlowRoute.ARRIVING_BUSES
            detailAdapter.setFlowRoute(flow)
            adapter = detailAdapter
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

    private fun displayViewWithTab() {
        if (viewModel.tabRoute == TabRoute.SUGGEST) {
            binding.rvSuggestedRoutes.isVisible = true
            binding.rvArrivingBuses.isVisible = false
            binding.tvArrivingYourStop.isVisible = false
        } else {
            binding.rvSuggestedRoutes.isVisible = false
            binding.rvArrivingBuses.isVisible = true
            binding.tvArrivingYourStop.isVisible = true
        }
        setArrivingYourStop(binding.sbDestination.getText())
    }

    private fun observeState() {
        collectState<HomeState, String>(
            selector = { it.currentKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbCurrentLocation.getText() != keyword) {
                binding.sbCurrentLocation.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

        collectState<HomeState, String>(
            selector = { it.destinationKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbDestination.getText() != keyword) {
                binding.sbDestination.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

        collectState<HomeState, List<RouteDetailItem>>(
            selector = {
                it.detailItems
            }
        ) {
            detailAdapter.submitList(it)
        }

    }

    fun View.setEnabledWithAlpha(enabledCurrentLocation: Boolean, enableDestination: Boolean) {
        isEnabled = enabledCurrentLocation && enableDestination
        alpha = if (enabledCurrentLocation && enableDestination) 1f else 0.65f
    }

    private fun initAction() {
        bindSearchBar(
            binding.sbCurrentLocation,
            popupCurrentLocation,
            HomeCommand.ClearCurrentLocation, {
                HomeCommand.SearchKeyLocation(it)
            }
        )

        bindSearchBar(
            binding.sbDestination,
            popupDestination,
            HomeCommand.ClearDestination,
            { HomeCommand.SearchKeyDestination(it) }
        )

        binding.segment.setOnTabSelectedListener {
            if (it == 0) {
                viewModel.tabRoute = TabRoute.SUGGEST
                val flow =
                    if (viewModel.tabRoute == TabRoute.SUGGEST) FlowRoute.SUGGEST_ROUTE else FlowRoute.ARRIVING_BUSES
                detailAdapter.setFlowRoute(flow)
                sendCommand(HomeCommand.GetSuggestRoutes(
                    LocationSearch(
                        viewModel.state.value.selectedCurrentLocation,
                        viewModel.state.value.selectedDestination
                    )
                ))
            } else {
                viewModel.tabRoute = TabRoute.ARRIVING
                val flow =
                    if (viewModel.tabRoute == TabRoute.SUGGEST) FlowRoute.SUGGEST_ROUTE else FlowRoute.ARRIVING_BUSES
                detailAdapter.setFlowRoute(flow)
                sendCommand(HomeCommand.GetNearbyRoutes(
                    LocationSearch(
                        viewModel.state.value.selectedCurrentLocation,
                        viewModel.state.value.selectedDestination
                    )
                ))
            }
            displayViewWithTab()

        }
        binding.lnFindRoute.setSafeOnClick {
            setArrivingYourStop(binding.sbDestination.getText())
            displayViewWithTab()
            if (viewModel.tabRoute == TabRoute.SUGGEST) {
                sendCommand(
                    HomeCommand.GetSuggestRoutes(
                        LocationSearch(
                            viewModel.state.value.selectedCurrentLocation,
                            viewModel.state.value.selectedDestination
                        )
                    )
                )
            } else {
                sendCommand(HomeCommand.GetNearbyRoutes(
                    LocationSearch(
                        viewModel.state.value.selectedCurrentLocation,
                        viewModel.state.value.selectedDestination
                    )
                ))
            }
        }
        binding.imBack.setSafeOnClick {
            onBackPressedDispatcher.onBackPressed()
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

    private fun bindSearchBar(
        searchBar: SearchInputView,
        popup: ListPopupWindow,
        clearCommand: Command,
        searchCommand: (String) -> Command
    ) {

        searchBar.setOnTextChangedListener { text ->
            if (text.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                sendCommand(searchCommand(text))
            }
        }

        searchBar.setOnFocusChangeListener { hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            val keyword = searchBar.getText()
            if (keyword.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                //sendCommand(searchCommand(keyword))
            }
        }
    }

    private fun initPopup() {
        adapterCurrentLocation = BaseDropdownAdapter(
            context = this,
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupCurrentLocation = createPopup(
            binding.viewAnchorCurrentLocation,
            adapterCurrentLocation
        ) {

            sendCommand(
                HomeCommand.SelectCurrentLocation(
                    it.name.orEmpty(),
                    it
                )
            )
        }
        adapterDestination = BaseDropdownAdapter(
            context = this,
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupDestination = createPopup(
            binding.viewAnchorDestination,
            adapterDestination
        ) {

            sendCommand(
                HomeCommand.SelectDestination(
                    it.name.orEmpty(),
                    it
                )
            )
        }
    }

    private fun createPopup(
        anchor: View,
        adapter: BaseDropdownAdapter<SearchLocation>,
        onItemClick: (SearchLocation) -> Unit
    ): ListPopupWindow {
        return ListPopupWindow(this).apply {
            anchorView = anchor
            width = anchor.width
            isModal = false
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let(onItemClick)
                dismiss()
            }
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is HomeEvent.SearchLocationSuccess -> {
                showPopupLocationSuggest(
                    popupCurrentLocation,
                    adapterCurrentLocation,
                    binding.viewAnchorCurrentLocation,
                    viewModel.state.value.selectedCurrentLocation,
                    event.data
                )
            }

            is HomeEvent.SearchDestinationSuccess -> {
                showPopupLocationSuggest(
                    popupDestination,
                    adapterDestination,
                    binding.viewAnchorDestination,
                    viewModel.state.value.selectedDestination,
                    event.data
                )
            }

            is HomeEvent.GetSuggestRoutesSuccess -> {
                if (event.data?.routeItems.isNullOrEmpty()) {
                    binding.tvEmptyView.isVisible = true
                    binding.tvEmptyView.text = getString(R.string.no_routes_found)
                    binding.tvArrivingYourStop.isVisible = false
                    binding.rvSuggestedRoutes.isVisible = false
                    binding.rvArrivingBuses.isVisible = false
                } else {
                    binding.tvEmptyView.isVisible = false
                    binding.rvSuggestedRoutes.isVisible = true
                    binding.rvArrivingBuses.isVisible = false
                    binding.tvArrivingYourStop.isVisible = false
                    adapter.submitList(event.data.routeItems)
                }
            }

            is HomeEvent.GetNearbyArrivalsSuccess -> {
                if (event.data?.listNearbyArrivalItem.isNullOrEmpty()) {
                    binding.tvEmptyView.isVisible = true
                    binding.tvEmptyView.text = getString(R.string.no_buses_found)
                } else {
                    binding.tvEmptyView.isVisible = false
                    adapterArrival.submitList(event.data.listNearbyArrivalItem)
                }
            }

            is HomeEvent.OpenRouteDetail -> {
                val detail = viewModel.state.value.routeDetail ?: return
                binding.tvRouteName.text = detail.routeName
                binding.tvFrequency.isVisible = viewModel.tabRoute == TabRoute.SUGGEST
                binding.tvDestination.text = if (viewModel.tabRoute == TabRoute.SUGGEST) {
                    getString(R.string.start_end_route, detail.outboundStops?.firstOrNull()?.stopName, detail.outboundStops?.lastOrNull()?.stopName)
                } else {
                    getString(R.string.plate_and_eta, viewModel.nearbyArrivalData?.plate, viewModel.nearbyArrivalData?.etaTime)
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            else -> {}
        }
    }

    private fun showPopupLocationSuggest(
        popup: ListPopupWindow,
        adapter: BaseDropdownAdapter<SearchLocation>,
        anchor: View,
        selected: SearchLocation?,
        data: List<SearchLocation>?
    ) {

        adapter.submitList(data.orEmpty())
        adapter.setSelected(selected)

        if (data.isNullOrEmpty()) {
            popup.dismiss()
            return
        }

        if (!popup.isShowing) {
            popup.width = anchor.width
            popup.show()
        }
    }

    private fun getTextCurrentLocation(location: SearchLocation?): String {
        return location?.name.orEmpty()
    }
}
