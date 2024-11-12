package com.example.sellcar.data

import android.net.Uri

data class Generation(
    val name: String,
    val yearMin: Int,
    val yearMax: Int,

    ) {
    fun getNameIfYearInRange(year: Int): String {
        return if (year in yearMin..yearMax) {
            name
        } else {
            ""
        }
    }
}