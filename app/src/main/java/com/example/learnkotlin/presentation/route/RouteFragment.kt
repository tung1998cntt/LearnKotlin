package com.example.learnkotlin.presentation.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.FragmentRouteBinding
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.RouteItem
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.home.HomeCommand
import com.example.learnkotlin.presentation.home.HomeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : BaseFragment<FragmentRouteBinding>() {

    override val viewModel: RouteViewModel by viewModels()
    private val adapter by lazy {
        RouteAdapter { route ->

            // click detail

        }
    }

    private val areaAdapter by lazy {
        AreaAdapter {
            sendCommand(
                HomeCommand.SelectArea(it)
            )
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRouteBinding {
        return FragmentRouteBinding.inflate(inflater, container, false)
    }

    override fun onInit() {
        initRecyclerView()
        sendCommand(HomeCommand.GetArea)
        sendCommand(HomeCommand.GetRoute)
        observeState()
        initAction()

    }

    private fun initAction() {
        binding.sbSearchRoute.setOnTextChangedListener {text ->
            sendCommand(
                HomeCommand.SearchRoute(
                    text
                )
            )
        }
        binding.imBack.setSafeOnClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeState() {
        collectState<RouteState, List<RouteItem>>(
            selector = { it.routes }
        ) {
            adapter.submitList(it)
        }

        collectState<RouteState, List<Area>>(
            selector = { it.areas }
        ) {
            areaAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        binding.rvRoutes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RouteFragment.adapter
        }

        binding.rvArea.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = areaAdapter
        }
        addLoadMore()
    }

    private fun addLoadMore() {

        binding.rvRoutes.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    if (dy <= 0) return
                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    val lastVisible =
                        layoutManager.findLastVisibleItemPosition()
                    val total =
                        layoutManager.itemCount
                    val state = viewModel.state.value
                    if (!state.loadingMore &&
                        state.hasNext &&
                        lastVisible >= total - 3
                    ) {
                        sendCommand(
                            HomeCommand.LoadMore
                        )

                    }
                }
            }
        )
    }



    override fun handleEvent(event: Any) {
        when (event) {
            is HomeEvent.GetRouteSuccess -> {

            }

            is UiEvent.Error -> {
                showErrorDialog(event.message, onConfirm = {

                })
            }
        }
    }
}
