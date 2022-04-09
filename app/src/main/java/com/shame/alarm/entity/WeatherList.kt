package com.shame.alarm.entity


import com.google.gson.annotations.SerializedName

data class WeatherList(
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("dt_txt")
    val dtTxt: String,
    @SerializedName("main")
    val main: Main,
    @SerializedName("pop")
    val pop: Float,
    @SerializedName("rain")
    val rain: Rain,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
)