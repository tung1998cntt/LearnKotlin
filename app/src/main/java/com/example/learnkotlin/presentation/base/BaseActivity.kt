package com.example.learnkotlin.presentation.base

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.learnkotlin.R
import com.example.learnkotlin.Tags
import com.example.learnkotlin.extensions.parcelable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: BaseViewModel

    /** Subclass cung cấp inflate binding */
    abstract fun inflateBinding(): VB

    /** Subclass xử lý event */
    abstract fun handleEvent(event: Any)

    /** Subclass khởi tạo UI / gửi command ban đầu */
    abstract fun onInit()

    private var lottieLoading: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding()
        setContentView(binding.root)
        initWindowInsets()
        initLottieLoading()
        observeState()
        observeEvents()
        viewModel.onInit()
        getNavData()?.let { viewModel.sendCommand(it) }
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
        lottieLoading = LottieAnimationView(this).apply {
            layoutParams = FrameLayout.LayoutParams(200, 200).apply { gravity = Gravity.CENTER }
            setAnimation(R.raw.animation_loading)
            repeatCount = LottieDrawable.INFINITE
            visibility = View.GONE
        }
        (binding.root as? FrameLayout)?.addView(lottieLoading)
    }

    private fun observeState() {
        lifecycleScope.launch {
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
        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                handleEvent(event)
            }
        }
        lifecycleScope.launch {
            viewModel.commands.collect { command ->
                when (command) {
                    is NavigationCommand.Navigate -> {
                        if (command.navigationRoute is NavigationRoute.ToActivity) {
                            val intent = Intent(this@BaseActivity, command.navigationRoute.clazz)
                            command.navigationRoute.data?.let {
                                intent.putExtra(Tags.DATA, it)
                            }
                            startActivity(intent)
                        } else if (command.navigationRoute is NavigationRoute.ToFragment) {
                            val fragmentInstance = command.navigationRoute.fragmentClass
                                .getDeclaredConstructor()
                                .newInstance() as Fragment
                            fragmentInstance.arguments = Bundle().apply {
                                command.navigationRoute.data?.let { putParcelable(Tags.DATA, it) }
                            }
                            addFragment(
                                command.navigationRoute.containerId,
                                fragmentInstance,
                                command.navigationRoute.addToBackStack
                            )
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

    protected fun addFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.commit {
            replace(containerId, fragment)
            if (addToBackStack) addToBackStack(null)
        }
    }

    protected fun getNavData(): UiCommand? {
        val navCommand = intent.parcelable<NavigationRoute>(Tags.DATA)
        return navCommand?.let { NavigationCommand.Navigate(navCommand) }
    }
}
