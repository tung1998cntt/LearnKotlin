package com.example.learnkotlin.presentation.ui.user

import androidx.activity.viewModels
import com.example.learnkotlin.databinding.ActivityUserDetailBinding
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.model.user.ProductNavData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserDetailActivity : BaseActivity<ActivityUserDetailBinding>() {

    override val viewModel: UserDetailViewModel by viewModels()
    override fun inflateBinding() = ActivityUserDetailBinding.inflate(layoutInflater)


    override fun handleEvent(event: Event) {
        when (event) {
            is UserEvent.ShowUser -> binding.tvUserContent.text = event.user.toString()
            is UserEvent.ShowError -> { /* handle error */
            }
            else -> {/* To do*/}
        }
    }
    override fun onInit() {
        val userNav = navData as? ProductNavData
        binding.tvUserTitle.text = userNav?.toString()
        sendCommand(UserCommand.AddUser("Xin chao"))
    }
}