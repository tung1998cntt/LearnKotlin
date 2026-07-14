package com.example.learnkotlin.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.learnkotlin.BuildConfig
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.FragmentProfileBinding
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.base.dialog.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val viewModel: ProfileViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onInit() {
        initView()
        initAction()
    }

    private fun initView() {
        binding.tvVersion.text = requireContext().getString(
            R.string.suribus_v_s_suriname_public_transport,
            BuildConfig.VERSION_NAME
        )
    }

    private fun initAction() {
        binding.tvSignOut.setSafeOnClick {
            ConfirmDialog(
                title = requireContext().getString(R.string.confirm_logout_title),
                message = getString(R.string.need_to_sign_back),
                confirmText = getString(R.string.confirm_title),
                cancelText = requireContext().getString(R.string.cancel_title),
                onConfirm = {
                    /*
                    * To do
                    * */
                },
                icon = R.drawable.ic_logout_40,
                showIcon = true
            ).show(parentFragmentManager, "ConfirmDialog")
        }
        binding.lnLanguage.setSafeOnClick {
            val dialog = LanguageDialog()
            dialog.onEnglish = {

            }
            dialog.onNederlands = {

            }
            dialog.show(childFragmentManager, "LanguageDialog")
        }

        binding.imEdit.setSafeOnClick {
            val dialog = ProfileDialog()
            dialog.onConfirmNo = {

            }
            dialog.onConfirmYes = {

            }
            dialog.show(childFragmentManager, "ProfileFragment")
        }

        binding.lnFeedback.setSafeOnClick {
            val dialog = FeedbackDialog()
            dialog.onConfirmNo = {

            }
            dialog.onConfirmYes = {

            }
            dialog.show(childFragmentManager, "FeedbackDialog")
        }

        binding.imBack.setSafeOnClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun handleEvent(event: Any) {
        when (event) {
            // Xử lý Event riêng của Profile nếu có
        }
    }
}