package com.example.learnkotlin.presentation.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.learnkotlin.databinding.FragmentUserBinding
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>() {

    override val viewModel: UserViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserBinding =
        FragmentUserBinding.inflate(inflater, container, false)

    override fun onInit() {
        sendCommand(UserCommand.AddUser("Ok bạn"))
    }

    override fun handleEvent(event: Any) {
        when (event) {
            is UserEvent.ShowUser -> binding.tvUserContent.text = event.user.toString()
            is UserEvent.ShowError -> { /* handle error */
            }
            else -> {/* To do*/}
        }
    }
}