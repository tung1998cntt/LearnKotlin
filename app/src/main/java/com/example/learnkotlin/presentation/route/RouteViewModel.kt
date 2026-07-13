package com.example.learnkotlin.presentation.route

import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.base.BaseError
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.RouteListRequest
import com.example.learnkotlin.domain.usecase.home.HomeUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.base.DialogEvent
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.home.HomeCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
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

            else -> super.handleCommand(command)
        }
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
            delay(300)
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
                                    routes + result.data.routes
                                else
                                    result.data.routes
                            copy(
                                routes = newRoutes,
                                hasNext = newRoutes.size < result.data.total,
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

}