package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.databinding.ActivityUserDetailBinding
import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.presentation.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDetailActivity : BaseActivity<ActivityUserDetailBinding>() {

    override val viewModel: UserDetailViewModel by viewModel()
    override fun inflateBinding() = ActivityUserDetailBinding.inflate(layoutInflater)

    override fun onInit() {
        val user: User = getNavData()
        binding.tvUserTitle.text = user?.name ?: "Unknown"
    }

    override fun handleEvent(event: Any) {


    }
}