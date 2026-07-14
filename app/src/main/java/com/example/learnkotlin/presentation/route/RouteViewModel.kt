package com.example.learnkotlin.presentation.route

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.R
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.RouteDetail
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.RouteListRequest
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.domain.usecase.home.HomeUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.home.HomeCommand
import com.example.learnkotlin.presentation.home.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<RouteState>() {

    private var offset = 0

    private val limit = 30

    private var searchJob: Job? = null
    private companion object {
        const val SEARCH_DELAY = 300L
    }


    override fun createInitialState() = RouteState()

    override fun onReady() {
        /* To do*/
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is HomeCommand.GetRoute -> loadRoute()
            is HomeCommand.LoadMore -> loadMore()

            is HomeCommand.GetArea -> {
                getAreaList()
            }

            is HomeCommand.SelectArea ->
                selectArea(command.area)

            is HomeCommand.SearchRoute -> searchRoute(command.keyword)

            is HomeCommand.GetRouteDetail -> {

                getRouteDetail(
                    command.routeId,
                    command.variant
                )

            }

            is HomeCommand.ChangeVariant -> {
                updateState {
                    copy(
                        variant = command.variant
                    )
                }
                val detail = state.value.routeDetail ?: return
                getRouteDetail(
                    id = detail.id ?: "",
                    variant = if (command.variant == Variant.OUTBOUND)
                        "outbound"
                    else
                        "inbound"
                )
            }

            is HomeCommand.ChangeSegment -> {
                updateState {
                    copy(
                        segment = command.segment
                    )
                }
                refreshDetailItems()
            }

            else -> super.handleCommand(command)
        }
    }

    private fun refreshDetailItems() {
        val detail = state.value.routeDetail ?: return
        updateState {
            copy(
                detailItems = buildDetailItems(
                    response = detail,
                    variant = variant,
                    segment = segment
                )
            )
        }
    }

    private fun buildDetailItems(
        response: RouteDetail,
        variant: Variant,
        segment: SegmentType
    ): List<RouteDetailItem> {

        val stops = when (variant) {
            Variant.OUTBOUND -> response.outboundStops
            Variant.INBOUND -> response.inboundStops
        }

        val items = mutableListOf<RouteDetailItem>()

        // 1. Summary
        items += RouteDetailItem.Summary(
            fare = "SRD 8", // lấy từ API nếu có
            distance = if (variant == Variant.OUTBOUND) {
                context.getString(R.string.km_value, response.outboundDistance)
            } else {
                context.getString(R.string.km_value, response.inboundDistance)
            },
            stopCount = stops?.size ?: 0
        )

        // 2. Direction
        items += RouteDetailItem.Direction(
            selected = variant
        )



//        val stopss = listOf(
//
//            RouteStop(
//                stopName = "Stop 1",
//                latitude = 21.0700,
//                longitude = 105.8000,
//                pathPoints = listOf(
//                    RoutePoint(105.8000, 21.0700),
//                    RoutePoint(105.8010, 21.0705),
//                    RoutePoint(105.8020, 21.0710)
//                )
//            ),
//
//            RouteStop(
//                stopName = "Stop 2",
//                latitude = 21.0710,
//                longitude = 105.8020,
//                pathPoints = listOf(
//                    RoutePoint(105.8020, 21.0710),
//                    RoutePoint(105.8030, 21.0720),
//                    RoutePoint(105.8040, 21.0730)
//                )
//            ),
//
//            RouteStop(
//                stopName = "Stop 3",
//                latitude = 21.0730,
//                longitude = 105.8040,
//                pathPoints = listOf(
//                    RoutePoint(105.8040, 21.0730),
//                    RoutePoint(105.8060, 21.0740),
//                    RoutePoint(105.8080, 21.0750)
//                )
//            ),
//
//            RouteStop(
//                stopName = "Stop 4",
//                latitude = 21.0750,
//                longitude = 105.8080,
//                pathPoints = emptyList() // Stop cuối thường không có đoạn đi tiếp
//            )
//        )
        // 3. Map
        items += RouteDetailItem.Map(
            points =
//                listOf(
//                RoutePoint(105.8000, 21.0700),
//                RoutePoint(105.8010, 21.0705),
//                RoutePoint(105.8020, 21.0710),
//                RoutePoint(105.8030, 21.0718),
//                RoutePoint(105.8040, 21.0725),
//                RoutePoint(105.8050, 21.0730),
//                RoutePoint(105.8060, 21.0735),
//                RoutePoint(105.8070, 21.0738),
//                RoutePoint(105.8080, 21.0740),
//                RoutePoint(105.8090, 21.0742),
//                RoutePoint(105.8100, 21.0745),
//                RoutePoint(105.8110, 21.0748),
//                RoutePoint(105.8120, 21.0750),
//            ),
                stops?.flatMap { it.pathPoints ?: listOf()} ?: listOf(),
            stops = stops ?: listOf()
        )

        // 4. Segment
        items += RouteDetailItem.Segment(
            selected = segment
        )

        when (segment) {

            SegmentType.ROUTE_INFORMATION -> {

                items += RouteDetailItem.Information(

                    operator = response.orgName ?: "Nationaal Vervoer Bed",

                    payment = "Cash (SRD), OmniCard, Mobile pay",

                    operatingHours = RouteDetailItem.OperatingHours(

                        day1 = "Mon - Fri",
                        time1 = "05:30 - 22:00",

                        day2 = "Saturday",
                        time2 = "06:30 - 22:00",

                        day3 = "Sunday & Holidays",
                        time3 = "07:00 - 22:00"
                    )
                )
            }

            SegmentType.BUS_STOP -> {

                stops?.forEachIndexed { index, stop ->

                    items += RouteDetailItem.Stop(

                        stop = stop,

                        // API chưa có khoảng cách
                        distanceText = "+0.6km",

                        isFirst = index == 0,

                        isLast = index == stops.lastIndex
                    )
                }
            }
        }

        return items
    }

    private fun getAreaList() {
        launchWithLoading(
            showLoading = true,
            block = {
                when (val result = homeUseCase.getAreaList()) {
                    is ApiResult.Success -> {
                        updateState {
                            copy(
                                areas = listOf(
                                    Area(
                                        id = "",
                                        name = "All areas",
                                        isSelected = true
                                    )
                                ) + result.data
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        //sendEvent(UiEvent.Error(result.message))
                        val fakeAreas = listOf(
                            Area("", "All areas", true),
                            Area("1", "Brokopondo", false),
                            Area("2", "Commenwijne", false),
                            Area("3", "Commenwijne", false)
                        )

                        updateState {
                            copy(
                                areas = fakeAreas
                            )
                        }
                    }
                }
            },
            customErrorHandler = null
        )

    }

    private fun selectArea(area: Area) {
        updateState {
            copy(
                selectedArea = area,
                areas = areas.map {
                    it.copy(
                        isSelected = it.id == area.id
                    )
                }

            )
        }
        offset = 0
        getRoute(
            isLoadMore = false,
            keyword = state.value.searchKeyword
        )
    }

    private fun loadRoute() {
        offset = 0
        getRoute(false,
            keyword = state.value.searchKeyword)

    }

    private fun loadMore() {

        if (state.value.loadingMore) return
        if (!state.value.hasNext) return
        updateState {
            copy(
                loadingMore = true
            )
        }
        offset += limit
        getRoute(true,
            keyword = state.value.searchKeyword
            )
    }

    private fun searchRoute(keyword: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY)
            updateState {
                copy(searchKeyword = keyword)
            }
            offset = 0
            getRoute(
                isLoadMore = false,
                keyword = keyword
            )
        }
    }

    private fun getRoute(
        isLoadMore: Boolean,
        keyword: String? = null
    ) {
        launchWithLoading(
            showLoading = true,
            block = {
                when (val result = homeUseCase.getRouteList(
                    RouteListRequest(
                        limit = limit,
                        offset = offset,
                        keySearch = keyword
                    )
                )) {
                    is ApiResult.Success -> {
                        updateState {
                            val newRoutes =
                                if (isLoadMore)
                                    (routes + (result.data.routes ?: listOf()))
                                else
                                    result.data.routes
                            copy(
                                routes = newRoutes ?: listOf(),
                                hasNext = (newRoutes?.size ?: 0) < result.data.total,
                                loadingMore = false
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        updateState {
                            copy(
                                loadingMore = false
                            )
                        }
                    }
                }
            },
            customErrorHandler = null
        )
    }


    private fun getRouteDetail(
        id:String? = null,
        variant:String
    ){

        launchWithLoading(
            showLoading = true,
            block = {
                when(
                    val result = homeUseCase.getRouteDetail(
                        id ?: "",
                        variant
                    )
                ){
                    is ApiResult.Success ->{
                        updateState {
                            copy(
                                routeDetail = result.data,
                                variant =
                                    if (variant == "outbound")
                                        Variant.OUTBOUND
                                    else
                                        Variant.INBOUND,
                                segment = SegmentType.ROUTE_INFORMATION
                            )
                        }
                        refreshDetailItems()
                        sendEvent(HomeEvent.OpenRouteDetail)
                    }
                    is ApiResult.Error ->{

                    }
                }
            }
        )
    }


}