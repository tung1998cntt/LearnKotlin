package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.databinding.ActivityUserDetailBinding
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.model.ProductNavData
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDetailActivity : BaseActivity<ActivityUserDetailBinding>() {

    override val viewModel: UserDetailViewModel by viewModel()
    override fun inflateBinding() = ActivityUserDetailBinding.inflate(layoutInflater)
    override fun handleEvent(event: UiEvent) {
        TODO("Not yet implemented")
    }

    override fun onInit() {
        val userNav = navData as? ProductNavData
        binding.tvUserTitle.text = userNav?.name ?: "Unknown"
    }
}