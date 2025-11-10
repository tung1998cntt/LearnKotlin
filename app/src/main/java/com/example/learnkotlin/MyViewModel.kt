package com.example.learnkotlin

import android.util.Log
import androidx.lifecycle.MutableLiveData

class MyViewModel(private val repository: MyRepository): BaseViewModel() {
     val accountLiveData = MutableLiveData<String>()

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

    override fun onCleared() {
        super.onCleared()
    }
}