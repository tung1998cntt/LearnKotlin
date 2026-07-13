package com.example.learnkotlin.presentation.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.presentation.state.UiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {


    private var pendingResultCallback: ((NavData?) -> Unit)? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    protected var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: BaseViewModel<*>

    /** Subclass cung cấp inflate binding */
    abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    /** Subclass xử lý event */
    abstract fun handleEvent(event: Any)

    /** Subclass khởi tạo UI / gửi command ban đầu */
    abstract fun onInit()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindowInsets()
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        UiEvent.Loading -> showLoading()
                        UiEvent.HideLoading -> hideLoading()
                        is UiEvent.Error -> {
                            hideLoading()
                            handleError(event.message)
                        }
                        else -> handleEvent(event)
                    }
                }
            }
        }
    }
    /**
     * Collect toàn bộ State.
     * Callback sẽ được gọi mỗi khi bất kỳ field nào trong State thay đổi.
     *
     * Ví dụ:
     * collectState<HomeState> { state ->
     *     render(state)
     * }
     */

    protected fun <S : UiState> collectState(
        block: suspend (S) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                @Suppress("UNCHECKED_CAST")
                (viewModel.state as StateFlow<S>).collect {
                    block(it)
                }

            }
        }
    }


    /**
     * Collect một phần của State.
     * Chỉ callback khi giá trị được selector trả về thay đổi
     * (distinctUntilChanged()).
     *
     * Ví dụ:
     * collectState<HomeState, User?>(
     *     selector = { it.user }
     * ) {
     *     showUser(it)
     * }
     */
    protected fun <S : UiState, T> collectState(
        selector: (S) -> T,
        block: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

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

    protected open fun showErrorDialog(message: String? = null,  onConfirm: (() -> Unit)? = null) {
        (activity as? BaseActivity<*>)?.showConfirmDialog(title = message, onConfirm = onConfirm)
    }
    protected open fun showLoading() {
        (activity as? BaseActivity<*>)?.showLoading()
    }

    protected open fun hideLoading() {
        (activity as? BaseActivity<*>)?.hideLoading()
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
}
