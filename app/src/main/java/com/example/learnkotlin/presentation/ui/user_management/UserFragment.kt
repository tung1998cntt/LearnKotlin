package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.databinding.FragmentUserBinding
import com.example.learnkotlin.presentation.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFragment : BaseFragment<FragmentUserBinding>() {

    override val viewModel: UserViewModel by viewModel()

    override fun inflateBinding(): FragmentUserBinding =
        FragmentUserBinding.inflate(layoutInflater)

    override fun onInit() {
        sendCommand(UserCommand.LoadUser(1))
    }

    override fun handleEvent(event: Any) {
        when (event) {
            is UserEvent.ShowUser -> binding.tvUserContent.text = event.name
            is UserEvent.ShowError -> { /* handle error */
            }
            else -> {/* To do*/}
        }
    }
}