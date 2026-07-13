package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.learnkotlin.databinding.FragmentProfileBinding
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val viewModel: ProfileViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onInit() {
    }

    override fun handleEvent(event: Any) {
        when (event) {
            // Xử lý Event riêng của Profile nếu có
        }
    }
}