package com.example.learnkotlin.presentation.home

import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.domain.model.home.LoginRequest
import com.example.learnkotlin.domain.model.home.NearbyArrivalRequest
import com.example.learnkotlin.domain.model.home.RoutePlanRequest
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.domain.usecase.home.HomeUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : BaseViewModel<HomeState>() {

    private var currentSearchJob: Job? = null
    private var destinationSearchJob: Job? = null

    private companion object {
        const val SEARCH_DEBOUNCE = 300L
    }

    override fun createInitialState() = HomeState()

    override fun onReady() {
        //getBusStops()
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

            is HomeCommand.ReverseLocation -> {
                reverseLocation(command)
            }

            is HomeCommand.FindRoute -> {
                val current = state.value.selectedCurrentLocation ?: return
                val destination = state.value.selectedDestination ?: return
                getSuggestRoutes(current, destination)
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


    private fun reverseLocation(
        command: HomeCommand.ReverseLocation
    ) {

        viewModelScope.launch {

            execute(
                block = {
                    homeUseCase.reverseLocation(
                        command.latitude,
                        command.longitude
                    )
                },
                onSuccess = { location ->

                    when (command.field) {

                        SelectedField.CURRENT -> {
                            selectCurrentLocation(
                                location.name.orEmpty(),
                                location
                            )
                        }

                        SelectedField.DESTINATION -> {
                            selectDestination(
                                location.name.orEmpty(),
                                location
                            )
                        }
                    }
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
    }

    private fun getSuggestRoutes(
        from: SearchLocation,
        to: SearchLocation
    ) {
        launchWithLoading(
            showLoading = true,
            block = {
                val request = RoutePlanRequest(
                    fromLat = 5.824683700032168,
                    fromLon = -55.154445192050275,
                    toLat = 5.830925943635606,
                    toLon = -55.140309563639505
                )
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

    private fun getNearbyArrivals(
        request: NearbyArrivalRequest
    ) {
        viewModelScope.launch {
            execute(
                block = {
                    homeUseCase.getNearbyArrivals(request)
                },
                onSuccess = { nearbyArrival ->
                    /* To do*/
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
    }


    fun getBusStops() {
        launchWithLoading(
            showLoading = false,
            block = {
                when (val result = homeUseCase.getBusStops()) {
                    is ApiResult.Success -> {
                        updateState {
                            copy(busStops = result.data.orEmpty())
                        }
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
