package com.example.learnkotlin.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit



inline fun <reified T : Activity> Activity.navigateToActivity(
    data: Parcelable? = null,
    finishCurrent: Boolean = false
) {
    val intent = Intent(this, T::class.java)
    data?.let { intent.putExtra("data", it) }
    startActivity(intent)
    if (finishCurrent) finish()
}

/** Navigate từ Fragment sang Activity, kèm data optional */
inline fun <reified T : Activity> Fragment.navigateToActivity(
    data: Parcelable? = null,
    finishCurrent: Boolean = false
) {
    val intent = Intent(requireContext(), T::class.java)
    data?.let { intent.putExtra("data", it) }
    startActivity(intent)
    if (finishCurrent) requireActivity().finish()
}





/** Add fragment vào container */
fun FragmentActivity.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    supportFragmentManager.commit {
        add(containerId, fragment, fragment::class.java.simpleName)
        if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
    }
}

/** Replace fragment trong container */
fun FragmentActivity.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    supportFragmentManager.commit {
        replace(containerId, fragment, fragment::class.java.simpleName)
        if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
    }
}

/** Show fragment nếu đã tồn tại, hide các fragment khác */
fun FragmentActivity.showFragment(containerId: Int, fragment: Fragment) {
    val existing = supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
    supportFragmentManager.commit {
        supportFragmentManager.fragments.forEach { hide(it) }
        if (existing != null) show(existing) else add(containerId, fragment, fragment::class.java.simpleName)
    }
}

/** Pop fragment khỏi back stack */
fun FragmentActivity.popFragment() {
    supportFragmentManager.popBackStack()
}

fun Fragment.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    parentFragmentManager.commit {
        add(containerId, fragment, fragment::class.java.simpleName)
        if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
    }
}

fun Fragment.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    parentFragmentManager.commit {
        replace(containerId, fragment, fragment::class.java.simpleName)
        if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
    }
}

fun Fragment.showFragment(containerId: Int, fragment: Fragment) {
    val existing = parentFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
    parentFragmentManager.commit {
        parentFragmentManager.fragments.forEach { hide(it) }
        if (existing != null) show(existing) else add(containerId, fragment, fragment::class.java.simpleName)
    }
}

fun Fragment.popFragment() {
    parentFragmentManager.popBackStack()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key) as? T
    }
}
