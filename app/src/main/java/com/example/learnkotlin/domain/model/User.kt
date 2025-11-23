package com.example.learnkotlin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(val id: Int, val name: String) : Parcelable