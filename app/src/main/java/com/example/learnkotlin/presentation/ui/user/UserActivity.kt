package com.example.learnkotlin.presentation.ui.user

import androidx.activity.viewModels
import com.example.learnkotlin.databinding.ActivityUserBinding
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.model.user.ProductNavData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : BaseActivity<ActivityUserBinding>() {

    override val viewModel: UserViewModel by viewModels()

    override fun inflateBinding(): ActivityUserBinding =
        ActivityUserBinding.inflate(layoutInflater)

    override fun handleEvent(event: Event) {
        when (event) {
            is UserEvent.ShowUser -> binding.tvUserContent.text = event.user.toString()
            is UserEvent.ShowAlert -> Snackbar.make(
                findViewById(android.R.id.content), // root view
                "Message hiển thị",
                Snackbar.LENGTH_LONG
            ).show()
            is UserEvent.ShowError -> { /* handle error */
            }
            else -> {/* To do*/}
        }
    }

    override fun onInit() {
        binding.btnUserConfirm.setOnClickListener {
            sendCommand(UserCommand.LoadUsers)
        }

        binding.btnUserNext.setOnClickListener {
            val productData = ProductNavData("p1", "Product1","20000")
            startActivity(UserDetailActivity::class.java, productData) { result ->
                val returnedProduct = result as? ProductNavData
                returnedProduct?.let {
                    //showSnackbar("Returned product: ${it.name}") // xử lý kết quả
                }
            }
        }

    }
}