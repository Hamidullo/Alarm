package com.shame.alarm.activitys

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shame.alarm.R
import com.shame.alarm.entity.Data
import com.shame.alarm.help.NetworkHelper
import com.squareup.picasso.Picasso

class WeatherActivity : AppCompatActivity() {
    private lateinit var networkHelper: NetworkHelper
    private lateinit var requestQueue: RequestQueue
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        bringDataLocation(location)
                    }
                }
        } else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PackageManager.PERMISSION_GRANTED
            )
        }
    }


    private fun bringData(){
        networkHelper = NetworkHelper(this)
        if (networkHelper.isNetworkConnected()){
            requestQueue = Volley.newRequestQueue(this)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                "https://api.openweathermap.org/data/2.5/forecast?q=Toshkent&units=metric&appid=87371da9baf6be746254d014e4eac004",
                null, { response ->
                    val type = object : TypeToken<Data>() {}.type
                    var data: Data = Gson().fromJson(response.toString(),type)
                    println(response.toString())

                    println(data.city.population)
                    println(data.city.country)
                    println(data.list[0].dtTxt)
                    println(data.list[0].weather[0].description)

                    setData(data)
                }
            ) {
                Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
            }
            requestQueue.add(jsonObjectRequest)
        } else {
            Toast.makeText(this,"No internet!!", Toast.LENGTH_SHORT).show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE
                ),
                PackageManager.PERMISSION_GRANTED
            )
        }
    }

    private fun bringDataLocation(location: Location){
        networkHelper = NetworkHelper(this)
        if (networkHelper.isNetworkConnected()){
            requestQueue = Volley.newRequestQueue(this)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                "https://api.openweathermap.org/data/2.5/forecast?lat=${location.latitude}&lon=${location.longitude}&units=metric&appid=87371da9baf6be746254d014e4eac004",
                null, { response ->
                    val type = object : TypeToken<Data>() {}.type
                    var data: Data = Gson().fromJson(response.toString(),type)
                    println(response.toString())

                    println(data.city.population)
                    println(data.city.country)
                    println(data.list[0].dtTxt)
                    println(data.list[0].weather[0].description)

                    setData(data)
                }
            ) {
                Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
            }
            requestQueue.add(jsonObjectRequest)
        } else {
            Toast.makeText(this,"No internet!!", Toast.LENGTH_SHORT).show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE
                ),
                PackageManager.PERMISSION_GRANTED
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        bringDataLocation(location)
                    }
                }
        } else{
            bringData()
        }

    }

    fun setData(list: Data){

        findViewById<TextView>(R.id.cityWeather).text = list.city.name

        val iconUrl = "https://openweathermap.org/img/wn/" + list.list[5].weather[0].icon  + "@2x.png"
        Picasso.get().load(iconUrl).into(findViewById<ImageView>(R.id.mainIconWeather));
        //Glide.with(this).load(iconUrl).into(findViewById<ImageView>(R.id.mainIconWeather))
        //Picasso.with(this).load(iconUrl).error(R.drawable.image16).into(findViewById<ImageView>(R.id.mainIconWeather))
        //findViewById<ImageView>(R.id.mainIconWeather)

        val iconUrl1 = "https://openweathermap.org/img/wn/" + list.list[5].weather[0].icon + "@2x.png"
        Picasso.get().load(iconUrl1).into(findViewById<ImageView>(R.id.iconWeatherItem));
        //Glide.with(this).load(iconUrl1).into(findViewById<ImageView>(R.id.mainIconWeather));
        //findViewById<ImageView>(R.id.iconWeatherItem)

        val iconUrl2 = "https://openweathermap.org/img/wn/" + list.list[13].weather[0].icon  + "@2x.png"
        Picasso.get().load(iconUrl2).into(findViewById<ImageView>(R.id.iconWeatherItem1));
        //Glide.with(this).load(iconUrl2).into(findViewById<ImageView>(R.id.iconWeatherItem1));
        //findViewById<ImageView>(R.id.iconWeatherItem1)

        val iconUrl3 = "https://openweathermap.org/img/wn/" + list.list[21].weather[0].icon + "@2x.png"
        Picasso.get().load(iconUrl3).into(findViewById<ImageView>(R.id.iconWeatherItem2));
        //Glide.with(this).load(iconUrl3).into(findViewById<ImageView>(R.id.iconWeatherItem2));
        //findViewById<ImageView>(R.id.iconWeatherItem2)

        val iconUrl4 = "https://openweathermap.org/img/wn/" + list.list[29].weather[0].icon  + "@2x.png"
        Picasso.get().load(iconUrl4).into(findViewById<ImageView>(R.id.iconWeatherItem3));
        //Glide.with(this).load(iconUrl4).into(findViewById<ImageView>(R.id.iconWeatherItem3));
        //findViewById<ImageView>(R.id.iconWeatherItem3)

        val iconUrl5 = "https://openweathermap.org/img/wn/" + list.list[37].weather[0].icon  + "@2x.png"
        Picasso.get().load(iconUrl5).into(findViewById<ImageView>(R.id.iconWeatherItem4));
        //Glide.with(this).load(iconUrl5).into(findViewById<ImageView>(R.id.iconWeatherItem3));
        //findViewById<ImageView>(R.id.iconWeatherItem4)

        findViewById<TextView>(R.id.tempWeather).text = "${list.list[5].main.temp}°C"
        findViewById<TextView>(R.id.temMaxMinWeather).text =  "${list.list[5].main.tempMin}°C/${list.list[5].main.tempMax}°C"

        findViewById<TextView>(R.id.humidityWeather).text = "${list.list[5].main.humidity}%"
        findViewById<TextView>(R.id.speedWeather).text = "${list.list[5].wind.speed}km/h"
        findViewById<TextView>(R.id.descriptionWeather).text = "${list.list[5].weather[0].main}"

        val text = list.list[0].dtTxt.split(" ")
        findViewById<TextView>(R.id.dateWeather).text = text[0]
        findViewById<TextView>(R.id.itemTempMinMax).text = "${list.list[0].main.tempMin}°C/${list.list[0].main.tempMax}°C"

        val text1 = list.list[5].dtTxt.split(" ")
        findViewById<TextView>(R.id.dateWeather1).text = text1[0]
        findViewById<TextView>(R.id.itemTempMinMax1).text = "${list.list[5].main.tempMin}°C/${list.list[5].main.tempMax}°C"

        val text2 = list.list[13].dtTxt.split(" ")
        findViewById<TextView>(R.id.dateWeather2).text = text2[0]
        findViewById<TextView>(R.id.itemTempMinMax2).text = "${list.list[13].main.tempMin}°C/${list.list[13].main.tempMax}°C"

        val text3 = list.list[21].dtTxt.split(" ")
        findViewById<TextView>(R.id.dateWeather3).text = text3[0]
        findViewById<TextView>(R.id.itemTempMinMax3).text = "${list.list[21].main.tempMin}°C/${list.list[21].main.tempMax}°C"

        val text4 = list.list[29].dtTxt.split(" ")
        findViewById<TextView>(R.id.dateWeather4).text = text4[0]
        findViewById<TextView>(R.id.itemTempMinMax4).text = "${list.list[29].main.tempMin}°C/${list.list[29].main.tempMax}°C"

    }

}