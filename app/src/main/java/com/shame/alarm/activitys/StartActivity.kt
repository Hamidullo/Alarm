package com.shame.alarm.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shame.alarm.MainActivity
import com.shame.alarm.R
import java.util.*

class StartActivity : AppCompatActivity() {
    private var counter = 0
    private val timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                Log.e("TimerTask", "$counter")
                counter++

                if(counter == 2){
                    openMapsActivity()
                }
            }
        }

        timer.schedule(timerTask, 0, 1000)
    }
    private fun openMapsActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        timer.cancel()
        finish()
    }
}