package com.example.learnkotlin.ui

import com.example.learnkotlin.base.BasePresenter
import com.example.learnkotlin.base.BaseView

interface MainContract {

    interface View : BaseView {
        fun showData(data: String)
        fun showTransactionList(data: List<String>)
    }

    interface Presenter: BasePresenter<View> {
        fun loadData()

        fun loadTransactions()
    }
}
