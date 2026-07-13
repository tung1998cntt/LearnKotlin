package com.example.learnkotlin.presentation.home

import android.view.View
import android.widget.ListPopupWindow
import androidx.activity.viewModels
import com.example.learnkotlin.databinding.ActivitySearchResultBinding
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.domain.base.customview.SearchInputView
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.base.baseDropdown.BaseDropdownAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>() {

    override val viewModel: SearchResultModel by viewModels()

    private lateinit var popupCurrentLocation: ListPopupWindow
    private lateinit var adapterCurrentLocation: BaseDropdownAdapter<SearchLocation>

    private lateinit var popupDestination: ListPopupWindow
    private lateinit var adapterDestination: BaseDropdownAdapter<SearchLocation>

    override fun inflateBinding(): ActivitySearchResultBinding =
        ActivitySearchResultBinding.inflate(layoutInflater)

    override fun onInit() {
        initView()
        initPopup()
        initAction()
        observeState()
    }
    private fun initView() {
        binding.lnFindRoute.setEnabledWithAlpha(
            !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
            !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
        )
    }

    private fun observeState() {
        collectState<HomeState, String>(
            selector = { it.currentKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbCurrentLocation.getText() != keyword) {
                binding.sbCurrentLocation.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

        collectState<HomeState, String>(
            selector = { it.destinationKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbDestination.getText() != keyword) {
                binding.sbDestination.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

    }

    fun View.setEnabledWithAlpha(enabledCurrentLocation: Boolean, enableDestination: Boolean) {
        isEnabled = enabledCurrentLocation && enableDestination
        alpha = if (enabledCurrentLocation && enableDestination) 1f else 0.65f
    }

    private fun initAction() {
        bindSearchBar(
            binding.sbCurrentLocation,
            popupCurrentLocation,
            HomeCommand.ClearCurrentLocation, {
                HomeCommand.SearchKeyLocation(it)
            }
        )

        bindSearchBar(
            binding.sbDestination,
            popupDestination,
            HomeCommand.ClearDestination,
            { HomeCommand.SearchKeyDestination(it) }
        )

    }

    private fun bindSearchBar(
        searchBar: SearchInputView,
        popup: ListPopupWindow,
        clearCommand: Command,
        searchCommand: (String) -> Command
    ) {

        searchBar.setOnTextChangedListener { text ->
            if (text.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                sendCommand(searchCommand(text))
            }
        }

        searchBar.setOnFocusChangeListener { hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            val keyword = searchBar.getText()
            if (keyword.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                //sendCommand(searchCommand(keyword))
            }
        }
    }

    private fun initPopup() {
        adapterCurrentLocation = BaseDropdownAdapter(
            context = this,
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupCurrentLocation = createPopup(
            binding.viewAnchorCurrentLocation,
            adapterCurrentLocation
        ) {

            sendCommand(
                HomeCommand.SelectCurrentLocation(
                    it.name.orEmpty(),
                    it
                )
            )
        }
        adapterDestination = BaseDropdownAdapter(
            context = this,
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupDestination = createPopup(
            binding.viewAnchorDestination,
            adapterDestination
        ) {

            sendCommand(
                HomeCommand.SelectDestination(
                    it.name.orEmpty(),
                    it
                )
            )
        }
    }

    private fun createPopup(
        anchor: View,
        adapter: BaseDropdownAdapter<SearchLocation>,
        onItemClick: (SearchLocation) -> Unit
    ): ListPopupWindow {
        return ListPopupWindow(this).apply {
            anchorView = anchor
            width = anchor.width
            isModal = false
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let(onItemClick)
                dismiss()
            }
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is HomeEvent.SearchLocationSuccess -> {
                showPopupLocationSuggest(
                    popupCurrentLocation,
                    adapterCurrentLocation,
                    binding.viewAnchorCurrentLocation,
                    viewModel.state.value.selectedCurrentLocation,
                    event.data
                )
            }

            is HomeEvent.SearchDestinationSuccess -> {
                showPopupLocationSuggest(
                    popupDestination,
                    adapterDestination,
                    binding.viewAnchorDestination,
                    viewModel.state.value.selectedDestination,
                    event.data
                )
            }

            else -> { /* To do*/
            }
        }
    }

    private fun showPopupLocationSuggest(
        popup: ListPopupWindow,
        adapter: BaseDropdownAdapter<SearchLocation>,
        anchor: View,
        selected: SearchLocation?,
        data: List<SearchLocation>?
    ) {

        adapter.submitList(data.orEmpty())
        adapter.setSelected(selected)

        if (data.isNullOrEmpty()) {
            popup.dismiss()
            return
        }

        if (!popup.isShowing) {
            popup.width = anchor.width
            popup.show()
        }
    }

    private fun getTextCurrentLocation(location: SearchLocation?): String {
        return location?.name.orEmpty()
    }

}