package com.example.learnkotlin.presentation.main

import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.state.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<EmptyState>() {
    override fun createInitialState() = EmptyState
}