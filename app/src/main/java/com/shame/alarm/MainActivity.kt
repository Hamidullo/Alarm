package com.shame.alarm

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shame.alarm.activitys.ListAlarmActivity
import com.shame.alarm.activitys.QRFileActivity
import com.shame.alarm.activitys.SetAlarmActivity
import com.shame.alarm.activitys.WeatherActivity
import com.shame.alarm.list.AlarmRecyclerViewAdapter
import com.shame.alarm.list.AlarmsListViewModel
import com.shame.alarm.list.OnToggleAlarmListener
import com.shame.alarm.model.Alarm

class MainActivity : AppCompatActivity(), OnToggleAlarmListener {
    private var alarmRecyclerViewAdapter: AlarmRecyclerViewAdapter? = null
    private var alarmsListViewModel: AlarmsListViewModel? = null
    private var alarmsRecyclerView: RecyclerView? = null
    private var alarms = ArrayList<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmRecyclerViewAdapter = AlarmRecyclerViewAdapter(this)
        alarmsListViewModel = ViewModelProviders.of(this).get(
            AlarmsListViewModel::class.java
        )
        alarmsListViewModel!!.alarmsLiveData.observe(this,
            { alarms ->
                if (alarms != null) {
                    alarmRecyclerViewAdapter!!.setAlarms(alarms)
                    this.alarms = alarms as ArrayList<Alarm>

                    if (alarms.size == 0){
                        alarmsRecyclerView!!.visibility = View.GONE
                        findViewById<TextView>(R.id.notAlarm).visibility = View.VISIBLE
                    } else {
                        alarmsRecyclerView!!.visibility = View.VISIBLE
                        findViewById<TextView>(R.id.notAlarm).visibility = View.GONE
                    }
                }
            })

        alarmsRecyclerView = findViewById(R.id.listAlarm)
        alarmsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        alarmsRecyclerView!!.adapter = alarmRecyclerViewAdapter


        alarmsRecyclerView!!.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        alarmRecyclerViewAdapter!!.setOnItemClickListener(object :
            AlarmRecyclerViewAdapter.onClickListner {
            override fun onItemClick(position: Int, v: View) {
                /*startActivity( Intent(this@MainActivity,SetAlarmActivity::class.java)
                    .putExtra("position",position))*/
            }

            override fun onItemLongClick(position: Int, v: View) {

                val dialog = Dialog(this@MainActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.checkbox3)

                val cancel: Button = dialog.findViewById(R.id.cancel) as Button
                cancel.setOnClickListener(View.OnClickListener {

                    dialog.dismiss()
                })

                val ok: Button = dialog.findViewById(R.id.ok) as Button
                ok.setOnClickListener(View.OnClickListener {
                    alarmsListViewModel!!.delete(alarms[position])

                    Toast.makeText(this@MainActivity,"Будильник ${alarms[position].title} удален!",
                        Toast.LENGTH_SHORT).show()

                    alarmsListViewModel!!.alarmsLiveData.observe(this@MainActivity,
                        { alarms ->
                            if (alarms != null) {
                                alarmRecyclerViewAdapter!!.setAlarms(alarms)
                                this@MainActivity.alarms = alarms as ArrayList<Alarm>
                            }
                        })
                    dialog.dismiss()
                })
                dialog.show()
            }
        })

        /*findViewById<ImageView>(R.id.addAlarm).setOnClickListener {
            startActivity(Intent(this, SetAlarmActivity::class.java))
        }*/

        findViewById<ImageView>(R.id.alarm).setOnClickListener {
            startActivity(Intent(this, SetAlarmActivity::class.java))
        }

        findViewById<ImageView>(R.id.weather).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        findViewById<ImageView>(R.id.more).setOnClickListener {
            startActivity(Intent(this, QRFileActivity::class.java))
        }
    }


    override fun onToggle(alarm: Alarm?) {
        if (alarm!!.isStarted) {
            alarm.cancelAlarm(applicationContext)
        } else {
            alarm.schedule(applicationContext)
        }
        alarmsListViewModel!!.update(alarm)
    }

}