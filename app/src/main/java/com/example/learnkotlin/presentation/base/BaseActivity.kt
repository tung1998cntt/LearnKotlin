package com.example.learnkotlin.presentation.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.Tags
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.presentation.model.NavData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: BaseViewModel

    private val launcherMap =
        mutableMapOf<Int, androidx.activity.result.ActivityResultLauncher<Intent>>()

    protected var navData: NavData? = null

    /** Subclass cung cấp inflate binding */
    abstract fun inflateBinding(): VB

    /** Subclass xử lý event (toast, navigate…) */
    abstract fun handleEvent(event: UiEvent)

    /** Subclass gửi command khởi tạo UI */
    abstract fun onInit()

    private var lottieLoading: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding()
        setContentView(binding.root)
        initWindowInsets()
        initLottieLoading()
        viewModel.onInit()
        navData = getNavDataParcelable()
        navData?.let { viewModel.sendCommand(InitDataCommand(it)) }
        viewModel.sendCommand(ReadyCommand())
        observeEvents()
        onInit()
    }

    /** Collect tất cả event từ ViewModel */
    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is UiEvent.Loading -> showLoading()
                    is UiEvent.HideLoading -> hideLoading()
                    is UiEvent.Error -> {
                        hideLoading()
                        handleError(event.message)
                    }
                }
            }
        }
    }

    protected open fun handleError(message: String?) = handleEvent(UiEvent.Error(message))

    protected fun sendCommand(command: Command) {
        viewModel.sendCommand(command)
    }

    protected open fun showLoading() {
        lottieLoading?.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    protected open fun hideLoading() {
        lottieLoading?.apply {
            cancelAnimation()
            visibility = View.GONE
        }
    }

    private fun initWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initLottieLoading() {
        lottieLoading = LottieAnimationView(this).apply {
            layoutParams = FrameLayout.LayoutParams(200, 200).apply { gravity = Gravity.CENTER }
            setAnimation(R.raw.animation_loading)
            repeatCount = LottieDrawable.INFINITE
            visibility = View.GONE
        }

        val rootView = binding.root
        if (rootView is ViewGroup) {
            rootView.addView(lottieLoading)
        } else {
            Log.w("BaseActivity", "Root view is not a ViewGroup, cannot add Lottie")
        }
    }

    protected inline fun <reified T : Parcelable> getNavDataParcelable(): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("data", T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("data") as? T
        }
    }


    protected fun <T : NavData> startActivity(
        clazz: Class<*>,
        data: T? = null,
        onResult: ((NavData?) -> Unit)? = null
    ) {
        val intent = Intent(this, clazz)
        data?.let { intent.putExtra("data", it) }

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultData = result.data?.getParcelableExtra<NavData>("data")
                onResult?.invoke(resultData)
            }
        launcher.launch(intent)
    }


}
