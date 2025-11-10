package com.example.learnkotlin.ui

import com.example.learnkotlin.base.BasePresenterImpl
import com.example.learnkotlin.data.repository.DataRepository

class MainPresenter(
    private val repository: DataRepository
) : BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    override fun loadData() {
        launchTask(
            task = { repository.getData() },
            onSuccess = { data -> view?.showData(data) }
        )
    }

    override fun loadTransactions() {
        launchTask(
            task = { repository.getTransactionList() },
            onSuccess = { list -> view?.showTransactionList(list) }
        )
    }
}