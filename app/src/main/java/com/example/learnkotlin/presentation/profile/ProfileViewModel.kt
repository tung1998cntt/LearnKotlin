package com.example.learnkotlin.presentation.profile

import com.example.learnkotlin.domain.repository.route.RouteRepository
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.state.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: RouteRepository
) : BaseViewModel<EmptyState>()  {
    override fun createInitialState() = EmptyState
    override fun onReady() {

        getNearbyBus()

    }

    private fun getNearbyBus() {

//        launchWithLoading {
//
//            execute(
//
//                block = {
//
//                    repository.getNearbyBus()
//
//                },
//
//                onSuccess = {
//
//                    sendEvent(HomeEvent.LoadBusSuccess(it))
//
//                }
//
//            )
//
//        }

    }

}