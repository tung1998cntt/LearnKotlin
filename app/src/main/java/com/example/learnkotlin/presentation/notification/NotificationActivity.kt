package com.example.learnkotlin.presentation.notification

import androidx.activity.viewModels
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.ActivityNotificationBinding
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {

    override val viewModel: NotificationViewModel by viewModels()

    private val adapter by lazy { NotificationAdapter() }

    override fun inflateBinding() = ActivityNotificationBinding.inflate(layoutInflater)

    override fun handleEvent(event: Event) {
        // Handle common events if needed
    }

    override fun onInit() {
        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.rvNotifications.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setSafeOnClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeState() {
        collectState<NotificationState> { state ->
            adapter.submitList(state.notifications)
        }
    }
}
