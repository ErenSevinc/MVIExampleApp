package com.example.mviexampleapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.TimeZone


@SuppressLint("SimpleDateFormat")
fun String.toDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
    val result = df.parse(this)
    val sdf = SimpleDateFormat("dd/MM/yyyy")
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(result)
}

