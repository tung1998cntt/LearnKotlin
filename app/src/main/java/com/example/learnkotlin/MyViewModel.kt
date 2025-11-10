package com.example.learnkotlin

import UiState
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class MyViewModel(private val repository: MyRepository): BaseViewModel() {
     val accountLiveData = MutableLiveData<String>()


    private val _accountStateFlow = MutableStateFlow<String>("Initial")
    val accountStateFlow: StateFlow<String> = _accountStateFlow

    private val _balanceStateFlow = MutableStateFlow<Float>(0f)
    val balanceStateFlow: StateFlow<Float> = _balanceStateFlow

    private val _transactionStateFlow = MutableStateFlow<List<String>>(emptyList())
    val transactionStateFlow: StateFlow<List<String>> = _transactionStateFlow

    /*
    *
    * combine → kết hợp giá trị latest của các StateFlow
    * stateIn → convert sang StateFlow, giữ latest value và chạy trong viewModelScope
    * */

    val compositeState: StateFlow<AccountUiState> = combine(
        _accountStateFlow,
        _balanceStateFlow,
        _transactionStateFlow
    ) { statusText, balance, transactions ->
        AccountUiState(
            statusText = statusText,
            balance = balance,
            transactions = transactions
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, AccountUiState("", 0f, emptyList()))


    private val _searchQuery = MutableStateFlow("")
    val searchResult: StateFlow<List<String>> = _searchQuery
        .debounce(300)            // chờ 300ms, tránh spam request
        .filter { it.isNotBlank() } // bỏ blank
        .flatMapLatest { query ->
            repository.searchItems(query).flowOn(Dispatchers.IO)   // Flow<List<String>>
                .catch { emit(emptyList()) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    fun fetchData() {
        launchData(
            block = {
                repository.fetchFromNetwork()
            },
            onState = { state ->
                when (state) {
                    is UiState.Loading -> {
                        val text = MyApp.getContext(AppData.getInstance().getLocale())
                            .getString(R.string.transfer_in_progress)
                        accountLiveData.value = text
                        Log.d(Tags.TUNG, text)
                    }
                    is UiState.Success -> {
                        val text = "Transfer success: ${state.data}"
                        accountLiveData.value = text
                        Log.d(Tags.TUNG, text)
                    }
                    is UiState.Error -> {
                        val text = "Transfer failed: ${state.exception}"
                        accountLiveData.value = text
                        Log.d(Tags.TUNG, text)
                    }
                    else -> {/* To do*/}
                }

            }
        )

    }

    fun fetchDataFlow() {
        launchData(
            block = { repository.fetchFromNetwork() },
            onState = { state ->
                when (state) {
                    is UiState.Loading -> {
                        val text = MyApp.getContext(AppData.getInstance().getLocale())
                            .getString(R.string.transfer_in_progress)
                        _accountStateFlow.value = text
                        Log.d(Tags.TUNG1, text)
                    }
                    is UiState.Success -> {
                        val text = "Transfer success: ${state.data}"
                        _accountStateFlow.value = text
                        Log.d(Tags.TUNG1, text)
                    }
                    is UiState.Error -> {
                        val text = "Transfer failed: ${state.exception}"
                        _accountStateFlow.value = text
                        Log.d(Tags.TUNG1, text)
                    }
                    else -> {/* To do*/}
                }
            }
        )
    }

    fun fetchBalance() {
        launchData(
            block = { repository.fetchBalance() },
            onState = { state ->
                when (state) {
                    is UiState.Success -> _balanceStateFlow.value = state.data
                    is UiState.Error -> Log.d(Tags.TUNG, "Fetch balance error: ${state.exception}")
                    else -> {}
                }
            }
        )
    }

    fun fetchTransactions() {
        launchData(
            block = { repository.fetchTransactions() },
            onState = { state ->
                when (state) {
                    is UiState.Success -> _transactionStateFlow.value = state.data
                    is UiState.Error -> Log.d(Tags.TUNG, "Fetch transaction error: ${state.exception}")
                    else -> {}
                }
            }
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    override fun onCleared() {
        super.onCleared()
    }
}