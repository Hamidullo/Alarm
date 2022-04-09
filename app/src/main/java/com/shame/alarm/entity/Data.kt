package com.shame.alarm.entity


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("cod")
    val cod: String,
    @SerializedName("message")
    val message: Int,
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("list")
    val list: List<WeatherList>,
    @SerializedName("city")
    val city: City
)