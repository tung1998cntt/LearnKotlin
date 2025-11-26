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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: BaseViewModel

    private val launcherMap =
        mutableMapOf<Int, androidx.activity.result.ActivityResultLauncher<Intent>>()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var pendingResultCallback: ((NavData?) -> Unit)? = null

    protected var navData: NavData? = null

    /** Subclass cung cấp inflate binding */
    abstract fun inflateBinding(): VB

    /** Subclass xử lý event (toast, navigate…) */
    abstract fun handleEvent(event: Event)

    /** Subclass gửi command khởi tạo UI */
    abstract fun onInit()

    private var lottieLoading: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding()
        setContentView(binding.root)
        initWindowInsets()
        initLottieLoading()
        registerActivityResultLauncher()
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
            viewModel.events.collect { event ->
                when (event) {
                    is UiEvent.Loading -> showLoading()
                    is UiEvent.HideLoading -> hideLoading()
                    else -> handleEvent(event)
                }
            }
        }
    }

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
        // Tạo Lottie AnimationView
        lottieLoading = LottieAnimationView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setAnimation(R.raw.animation_loading)
            repeatCount = LottieDrawable.INFINITE
            visibility = View.GONE
        }

        // Lấy root view của activity
        val rootView = findViewById<ViewGroup>(android.R.id.content)

        // Nếu rootView là FrameLayout hoặc ViewGroup, add Lottie vào
        if (rootView != null) {
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


    private fun registerActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultData = result.data?.getParcelableExtra<NavData>("data")
            pendingResultCallback?.invoke(resultData)
            pendingResultCallback = null
        }
    }

    protected fun <T : NavData> startActivity(
        clazz: Class<*>,
        data: T? = null,
        onResult: ((NavData?) -> Unit)? = null
    ) {
        val intent = Intent(this, clazz)
        data?.let { intent.putExtra("data", it) }

        pendingResultCallback = onResult
        activityResultLauncher.launch(intent)
    }

    protected fun startFragment(
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true,
        replace: Boolean = true,
        tag: String? = null,
        enterAnim: Int? = null,
        exitAnim: Int? = null
    ) {
        val transaction = supportFragmentManager.beginTransaction()

        // Animation nếu có
        if (enterAnim != null && exitAnim != null) {
            transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
        }

        if (replace) {
            transaction.replace(containerId, fragment, tag)
        } else {
            transaction.add(containerId, fragment, tag)
        }

        if (addToBackStack) transaction.addToBackStack(tag)
        transaction.commit()
    }

}
