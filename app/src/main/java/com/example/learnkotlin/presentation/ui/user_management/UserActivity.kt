package com.example.learnkotlin.presentation.ui.user_management

import android.widget.Toast
import com.example.learnkotlin.databinding.ActivityUserBinding
import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.extensions.navigateToActivity
import com.example.learnkotlin.presentation.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity : BaseActivity<ActivityUserBinding>() {

    override val viewModel: UserViewModel by viewModel()

    override fun inflateBinding(): ActivityUserBinding =
        ActivityUserBinding.inflate(layoutInflater)

    override fun onInit() {
        // gửi command load user khi Activity khởi tạo
        sendCommand(UserCommand.LoadUser(1))

        // refresh user bằng nút
        binding.btnUserConfirm.setOnClickListener {
            //sendCommand(UserCommand.RefreshUser)
            navigateToActivity<UserDetailActivity>(data = User(2, "BachTung"))

        }
    }

    override fun handleEvent(event: Any) {
        when (event) {
            is UserEvent.ShowUser -> {
                binding.tvUserContent.text = event.name
            }

            is UserEvent.ShowError -> {
                Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
            }

            else -> {/* to Do*/
            }
        }
    }
}