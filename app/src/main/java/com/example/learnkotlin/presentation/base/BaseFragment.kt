package com.example.learnkotlin.presentation.base

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.extensions.parcelable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

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
        observeState()
        observeEvents()
        viewModel.onInit()
        onInit()
    }

    private fun initWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initLottieLoading() {
        lottieLoading = LottieAnimationView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(200, 200).apply { gravity = Gravity.CENTER }
            setAnimation(R.raw.animation_loading)
            repeatCount = LottieDrawable.INFINITE
            visibility = View.GONE
        }
        (binding.root as? FrameLayout)?.addView(lottieLoading)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is BaseUiState.Loading -> showLoading()
                    is BaseUiState.Success -> hideLoading()
                    is BaseUiState.Error -> hideLoading()
                    is BaseUiState.Empty -> hideLoading()
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                handleEvent(event)
            }
        }
        lifecycleScope.launch {
            viewModel.commands.collect { command ->
                when (command) {
                    is NavigationRoute.ToActivity<*> -> {
                        val intent = Intent(requireContext(), command.clazz)
                        if (command.data is Parcelable) intent.putExtra("data", command.data)
                        startActivity(intent)
                    }
                    is NavigationRoute.ToFragment<*> -> {
                        command.fragment.arguments = Bundle().apply {
                            if (command.data is Parcelable) putParcelable("data", command.data)
                        }
                        parentFragmentManager.commit {
                            replace(command.containerId, command.fragment)
                            if (command.addToBackStack) addToBackStack(null)
                        }
                    }
                }
            }
        }
    }

    protected fun sendCommand(command: Any) {
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


    protected inline fun <reified T : Parcelable> getNavData(): T? {
        return arguments?.parcelable("data", T::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
