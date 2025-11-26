package com.example.learnkotlin.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.domain.base.Command
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {


    private var pendingResultCallback: ((NavData?) -> Unit)? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    protected var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: BaseViewModel

    /** Subclass cung cấp inflate binding */
    abstract fun inflateBinding(): VB

    /** Subclass xử lý event */
    abstract fun handleEvent(event: Any)

    /** Subclass khởi tạo UI / gửi command ban đầu */
    abstract fun onInit()

    private var lottieLoading: LottieAnimationView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = inflateBinding()
        initWindowInsets()
        initLottieLoading()
        registerActivityResultLauncher()
        viewModel.onInit()
        observeEvents()
        onInit()
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

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    UiEvent.Loading -> showLoading()
                    UiEvent.HideLoading -> hideLoading()
                    is UiEvent.Error -> {
                        hideLoading()
                        handleError(event.message)
                    }
                }
            }
        }
    }

    /** Start Activity từ Fragment chuẩn */
    protected fun <T : NavData> startActivity(
        clazz: Class<*>,
        data: T? = null,
        onResult: ((NavData?) -> Unit)? = null
    ) {
        val intent = Intent(requireContext(), clazz)
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
        val fm = parentFragmentManager
        val transaction = fm.beginTransaction()

        if (enterAnim != null && exitAnim != null) {
            transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
        }

        if (replace) transaction.replace(containerId, fragment, tag)
        else transaction.add(containerId, fragment, tag)

        if (addToBackStack) transaction.addToBackStack(tag)
        transaction.commit()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        lottieLoading = LottieAnimationView(requireContext()).apply {
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
        val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content)

        // Nếu rootView là FrameLayout hoặc ViewGroup, add Lottie vào
        if (rootView != null) {
            rootView.addView(lottieLoading)
        } else {
            Log.w("BaseActivity", "Root view is not a ViewGroup, cannot add Lottie")
        }
    }
}
