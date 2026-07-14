package com.example.learnkotlin.presentation.home

import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.domain.model.home.NearbyArrivalRequest
import com.example.learnkotlin.domain.model.home.RoutePlanRequest
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.usecase.home.HomeUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : BaseViewModel<HomeState>() {

    private var currentSearchJob: Job? = null
    private var destinationSearchJob: Job? = null

    var tabRoute: TabRoute = TabRoute.SUGGEST

    private companion object {
        const val SEARCH_DEBOUNCE = 300L
    }

    override fun createInitialState() = HomeState()

    override fun onReady() {


    }

    private fun searchLocation(keyword: String) {
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE)
            search(
                keyword = keyword,
                updateState = {
                    copy(
                        currentKeyword = it,
                        selectedCurrentLocation = null
                    )
                },
                successEvent = {
                    HomeEvent.SearchLocationSuccess(it)
                }
            )
        }
    }

    private fun searchDestination(keyword: String) {
        destinationSearchJob?.cancel()
        destinationSearchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE)

            search(
                keyword = keyword,
                updateState = {
                    copy(
                        destinationKeyword = it,
                        selectedDestination = null
                    )
                },
                successEvent = {
                    HomeEvent.SearchDestinationSuccess(it)
                }
            )
        }
    }


    override fun handleCommand(command: Command) {
        when (command) {
            is HomeCommand.SearchKeyLocation -> {
                searchLocation(command.keyword)
            }

            is HomeCommand.SearchKeyDestination -> {
                searchDestination(command.keyword)
            }

            is HomeCommand.SelectCurrentLocation -> {
                selectCurrentLocation(command.keyword, command.location)
            }

            is HomeCommand.SelectDestination -> {
                selectDestination(command.keyword, command.location)
            }

            is HomeCommand.ClearCurrentLocation -> {
                updateState {
                    copy(
                        currentKeyword = "",
                        selectedCurrentLocation = null
                    )
                }
            }

            is HomeCommand.ClearDestination -> {
                updateState {
                    copy(
                        destinationKeyword = "",
                        selectedDestination = null
                    )
                }
            }

            is HomeCommand.GetSuggestRoutes -> {
                val from = command.locationSearch?.selectedCurrentLocation
                val to = command.locationSearch?.selectedDestination

                // Cập nhật state để Activity có thể nhận được qua observer
                updateState {
                    copy(
                        currentKeyword = from?.name.orEmpty(),
                        selectedCurrentLocation = from,
                        destinationKeyword = to?.name.orEmpty(),
                        selectedDestination = to
                    )
                }

                if (from != null && to != null) {
                    getSuggestRoutes(from, to)
                }
            }

            is HomeCommand.GetNearbyRoutes -> {
                val from = command.locationSearch?.selectedCurrentLocation
                if (from != null) {
                    getNearbyArrivals(from)
                }
            }

            else -> super.handleCommand(command)
        }
    }


    private suspend fun search(
        keyword: String,
        updateState: HomeState.(String) -> HomeState,
        successEvent: (List<SearchLocation>) -> Event
    ) {
        execute(
            block = {
                homeUseCase.searchLocation(keyword)
            },
            onSuccess = { result ->
                updateState {
                    updateState(keyword)
                }
                sendEvent(successEvent(result))
            },
            onError = {
                sendEvent(
                    HomeEvent.ShowError(
                        it.message ?: "Unknown error"
                    )
                )
            }
        )
    }

    private fun selectCurrentLocation(currentKeyword: String = "", location: SearchLocation?) {
        updateState {
            copy(currentKeyword = currentKeyword, selectedCurrentLocation = location)
        }
    }

    private fun selectDestination(currentKeyword: String = "", location: SearchLocation?) {
        updateState {
            copy(destinationKeyword = currentKeyword, selectedDestination = location)
        }
    }

    private fun getSuggestRoutes(
        from: SearchLocation,
        to: SearchLocation
    ) {
        launchWithLoading(
            showLoading = true,
            block = {
                val request = RoutePlanRequest(
                fromLat = from.latitude ?: 0.0,
                fromLon = from.longitude ?: 0.0,
                toLat = to.latitude ?: 0.0,
                toLon = to.longitude ?: 0.0
            )
//                val request = RoutePlanRequest(
//                    fromLat = 5.824683700032168,
//                    fromLon = -55.154445192050275,
//                    toLat = 5.830925943635606,
//                    toLon = -55.140309563639505
//                )
                when (val result =  homeUseCase.getSuggestRoutes(request)) {
                    is ApiResult.Success -> {
                        sendEvent(HomeEvent.GetSuggestRoutesSuccess(result.data))
                    }

                    is ApiResult.Error -> {
                        sendEvent(
                            HomeEvent.ShowError(
                                result.message ?: "Unknown error"
                            )
                        )
                    }
                }
            },
            customErrorHandler = null
        )
    }

    private fun getNearbyArrivals(selectedCurrentLocation: SearchLocation? = null) {
        launchWithLoading(
            showLoading = true,
            block = {
                val request = NearbyArrivalRequest(
                    lat = selectedCurrentLocation?.latitude ?: 0.0,
                    lon = selectedCurrentLocation?.longitude ?: 0.0,
                    radiusMeters = 2000,
                    limit = 30
                )
//                val request = NearbyArrivalRequest(
//                    lat = 5.824683700032168,
//                    lon = -55.154445192050275,
//                    radiusMeters = 2000,
//                    limit = 30
//                )
                when (val result =  homeUseCase.getNearbyArrivals(request)) {
                    is ApiResult.Success -> {
                        sendEvent(HomeEvent.GetNearbyArrivalsSuccess(result.data))
                    }

                    is ApiResult.Error -> {
                        sendEvent(
                            HomeEvent.ShowError(
                                result.message ?: "Unknown error"
                            )
                        )
                    }
                }
            },
            customErrorHandler = null
        )
    }

}