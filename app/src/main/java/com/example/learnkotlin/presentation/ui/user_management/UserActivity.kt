package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.databinding.ActivityUserBinding
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.model.ProductNavData
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity : BaseActivity<ActivityUserBinding>() {

    override val viewModel: UserViewModel by viewModel()

    override fun inflateBinding(): ActivityUserBinding =
        ActivityUserBinding.inflate(layoutInflater)

    override fun handleEvent(event: UiEvent) {
        TODO("Not yet implemented")
    }

    override fun onInit() {
        binding.btnUserConfirm.setOnClickListener {
            sendCommand(UserCommand.LoadUsers)
        }

        binding.btnUserNext.setOnClickListener {
            val productData = ProductNavData("p1", "Product1","20000")
            startActivity(UserDetailActivity::class.java, productData) { result ->
                val productResult = result as? ProductNavData
            }
        }

    }
}