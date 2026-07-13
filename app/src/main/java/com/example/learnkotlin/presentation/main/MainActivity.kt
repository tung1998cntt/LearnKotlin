package com.example.learnkotlin.presentation.main

import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.learnkotlin.R
import com.example.learnkotlin.databinding.ActivityMainBinding
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.base.BaseActivity
import com.example.learnkotlin.presentation.home.HomeFragment
import com.example.learnkotlin.presentation.profile.ProfileFragment
import com.example.learnkotlin.presentation.route.RouteFragment
import com.example.learnkotlin.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val viewModel: MainViewModel by viewModels()

    private val homeFragment by lazy { HomeFragment() }
    private val routeFragment by lazy { RouteFragment() }
    private val profileFragment by lazy { ProfileFragment() }
    private lateinit var activeFragment: Fragment

    companion object {
        private const val TAG_HOME = "HOME"
        private const val TAG_ROUTE = "ROUTE"
        private const val TAG_PROFILE = "PROFILE"
    }

    override fun inflateBinding() =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onInit() {
        setupBottomNavigation()
        setupBackPressed()
        applyInsets(
            binding.bottomNavigation,
            applyNavigationBar = true
        )
    }

    override fun handleEvent(event: Event) {

    }

    private fun setupBottomNavigation() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentContainer, homeFragment, TAG_HOME)
        }

        activeFragment = homeFragment

        binding.bottomNavigation.setOnItemSelectedListener {
            val (target, tag) = when (it.itemId) {
                R.id.menuHome -> homeFragment to TAG_HOME
                R.id.menuRoutes -> routeFragment to TAG_ROUTE
                R.id.menuProfile -> profileFragment to TAG_PROFILE
                else -> return@setOnItemSelectedListener false
            }
            switchFragment(target, tag)
            true
        }
    }

    private fun switchFragment(target: Fragment, tag: String) {
        if (target == activeFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            hide(activeFragment)
            if (target.isAdded) {
                show(target)
            } else {
                add(R.id.fragmentContainer, target, tag)
            }
        }
        activeFragment = target
    }

    private fun setupBackPressed() {

        onBackPressedDispatcher.addCallback(this) {
            if (activeFragment != homeFragment) {
                binding.bottomNavigation.selectedItemId = R.id.menuHome
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()

            }
        }
    }

}