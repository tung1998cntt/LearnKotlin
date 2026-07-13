package com.example.learnkotlin.presentation.base

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.base.dialog.ConfirmDialog
import com.example.learnkotlin.presentation.state.UiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: BaseViewModel<*>

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
    private var loadingOverlay: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = inflateBinding()
        setContentView(binding.root)
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.Loading -> showLoading()
                        is UiEvent.HideLoading -> hideLoading()
                        is DialogEvent.ShowError -> {
                            showConfirmDialog(title = event.message, onConfirm = {

                            })
                        }

                        else -> handleEvent(event)
                    }
                }
            }
        }
    }

    protected fun <S : UiState> collectState(
        block: suspend (S) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                @Suppress("UNCHECKED_CAST")
                (viewModel.state as StateFlow<S>).collect {
                    block(it)
                }

            }
        }
    }

    protected fun <S : UiState, T> collectState(
        selector: (S) -> T,
        block: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                @Suppress("UNCHECKED_CAST")
                (viewModel.state as StateFlow<S>)
                    .map(selector)
                    .distinctUntilChanged()
                    .collect {
                        block(it)
                    }

            }
        }
    }

    protected fun sendCommand(command: Command) {
        viewModel.sendCommand(command)
    }

    open fun showLoading() {
        if (isFinishing || isDestroyed) return
        loadingOverlay?.visibility = View.VISIBLE
        lottieLoading?.playAnimation()
    }

    open fun hideLoading() {
        lottieLoading?.cancelAnimation()
        loadingOverlay?.visibility = View.GONE
    }

    protected fun applyInsets(
        view: View,
        applyStatusBar: Boolean = false,
        applyNavigationBar: Boolean = false
    ) {

        val startTop = view.paddingTop
        val startBottom = view.paddingBottom
        val startLeft = view.paddingLeft
        val startRight = view.paddingRight
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigation = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(
                startLeft,
                startTop + if (applyStatusBar) status.top else 0,
                startRight,
                startBottom + if (applyNavigationBar) navigation.bottom else 0
            )
            insets
        }

        ViewCompat.requestApplyInsets(view)
    }


    // Hàm initLottieLoading nên kiểm tra tránh add nhiều lần (trong trường hợp gọi lại onCreate)
    private fun initLottieLoading() {
        val root = window.decorView as ViewGroup

        loadingOverlay = FrameLayout(this).apply {

            layoutParams = FrameLayout.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
            visibility = View.GONE
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
            elevation = 1000f
            // hoặc
            // translationZ = 1000f
            setBackgroundColor(0x55000000)
            lottieLoading = LottieAnimationView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    WRAP_CONTENT,
                    WRAP_CONTENT,
                    Gravity.CENTER
                )
                setAnimation(R.raw.animation_loading)
                repeatCount = LottieDrawable.INFINITE
            }
            addView(lottieLoading)
        }
        root.addView(loadingOverlay)
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

    fun showConfirmDialog(
        title: String? = null,
        message: String? = null,
        onConfirm: (() -> Unit)? = null
    ) {
        // Kiểm tra nếu activity đang finish hoặc destroyed
        if (!isFinishing && !isDestroyed) {
            ConfirmDialog(
                title = title,
                message = message,
                onConfirm = onConfirm
            ).show(supportFragmentManager, "ConfirmDialog")
        }
    }

    /** Tự động ẩn bàn phím và nhả focus khi click ra ngoài bất kỳ EditText nào */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                // Nếu điểm chạm nằm ngoài vùng bounds của EditText đang focus
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}
