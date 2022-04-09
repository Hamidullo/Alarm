package com.shame.alarm.activitys

import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.shame.alarm.R
import com.shame.alarm.alarm.CreateAlarm
import com.shame.alarm.list.AlarmsListViewModel
import com.shame.alarm.model.Alarm
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln

class SetAlarmActivity : AppCompatActivity() {
    companion object {
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
        private const val MAX_VOLUME = 100
    }

    private var alarmsListViewModel: AlarmsListViewModel? = null
    private var alarms: ArrayList<Alarm> = ArrayList<Alarm>()

    private var name: String = "name"
    private var hour: Int = 9
    private var minute: Int = 30
    private var recurring: Boolean = false
    private var type: String = "vibration"
    private var typeOff: String = "Встряска"
    private var heavy: String = "easy"
    private var repeatAlarm = ArrayList<String>()
    private var ringtone: String = "default"
    private var ringtoneType = ArrayList<String>()
    private var volume: Float = 100f

    var types = arrayOf(
        "Встряска",
        "Примеры",
        "QR коде"
    )

    var days = arrayOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    private lateinit var toolbar: Toolbar

    private lateinit var nameText: EditText

    private lateinit var timeText: TextView

    private lateinit var easyText: TextView
    private lateinit var averageText: TextView
    private lateinit var heavyText: TextView

    private lateinit var spinner: Spinner

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var createAlarm: CreateAlarm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        ringtoneType.add("ringtone")

        val intent = intent.getIntExtra("position",-1)

        alarmsListViewModel = ViewModelProviders.of(this).get(
            AlarmsListViewModel::class.java
        )
        alarmsListViewModel!!.alarmsLiveData.observe(this,
            { alarms ->
                if (alarms != null) {

                    this.alarms = alarms as ArrayList<Alarm>


                }
            })
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner = findViewById<View>(R.id.types) as Spinner
        spinner.adapter = adapter
        spinner.prompt = types[0]

        createAlarm = ViewModelProviders.of(this)[CreateAlarm::class.java]

        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolBar)
        nameText = findViewById<EditText>(R.id.nameAlarm)
        timeText = findViewById<TextView>(R.id.timeAlarm)
        timeText.text = SimpleDateFormat("HH:mm").format(Date())
        hour = SimpleDateFormat("HH").format(Date()).toInt()
        minute = SimpleDateFormat("mm").format(Date()).toInt()

        easyText = findViewById<TextView>(R.id.easyType)
        averageText = findViewById<TextView>(R.id.averageType)
        heavyText = findViewById<TextView>(R.id.heavyType)

        setSupportActionBar(toolbar)

        timeText.setOnClickListener{
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

                this.hour = hour
                this.minute = minute
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                timeText.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show()
        }

        findViewById<ImageView>(R.id.playRingtone).setOnClickListener {

            if (mediaPlayer != null){
                if (mediaPlayer!!.isPlaying){
                    mediaPlayer!!.pause();
                } else{
                    try {
                        mediaPlayer!!.start()
                    } catch (exception: IOException) {
                        Toast.makeText(this,"Музыка не найдена!",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this,"Выберите музыку!",Toast.LENGTH_SHORT).show()
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                when (position) {
                    0 -> {
                        typeOff = "Встряска"
                        selectImage(1)
                    }
                    1 -> {
                        typeOff = "Примеры"
                        selectImage(2)
                    }
                    else -> {
                        typeOff = "QR коде"
                        selectImage(3)
                    }
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        findViewById<LinearLayout>(R.id.conteynerRingtone).setOnClickListener {

            /*val root = File( "/storage/self/primary/Music/")
            val uri = Uri.fromFile(root)
            val intent: Intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = uri
            startActivityForResult( intent, 89)*/

            val intent: Intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/"),"audio/*")
            startActivityForResult(Intent.createChooser(intent, "Ringtone"), 89)

        }

        println(Environment.getExternalStorageDirectory().getPath() + "/Music/")

        findViewById<SeekBar>(R.id.volume).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                volume = ((1 - ln((MAX_VOLUME - progress).toDouble()) / ln(
                    MAX_VOLUME.toDouble()
                )).toFloat())


                if (mediaPlayer != null){
                    mediaPlayer!!.setVolume(volume, volume)

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === 89 && resultCode === RESULT_OK) {
            if (data?.data != null) {

                var uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                uri = data.data!!

                /// /storage/self/primary/Music

                ringtone =
                    getPath(this,uri).toString() //"content://com.android.providers.media.documents" + uri.encodedPath.toString()

                println(ringtone)
                // dat=content://com.android.providers.media.documents/document/audio:235663
                if (mediaPlayer != null){
                    if (mediaPlayer!!.isPlaying){
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        mediaPlayer = null
                        mediaPlayer = MediaPlayer()
                        val mUri = Uri.parse( ringtone)  //
                        mediaPlayer!!.setDataSource(this, mUri)
                        mediaPlayer!!.setVolume(volume,volume)
                        mediaPlayer!!.prepare()
                        Toast.makeText(this,"Музыка выбрана!",Toast.LENGTH_SHORT).show()
                    } else {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        mediaPlayer = null
                        mediaPlayer = MediaPlayer()
                        val mUri = Uri.parse( ringtone)  //
                        mediaPlayer!!.setDataSource(this, mUri)
                        mediaPlayer!!.setVolume(volume,volume)
                        mediaPlayer!!.prepare()
                        Toast.makeText(this,"Музыка выбрана!",Toast.LENGTH_SHORT).show()
                    }
                } else if (mediaPlayer == null){
                    mediaPlayer = MediaPlayer()
                    val mUri = Uri.parse( ringtone)  //
                    mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mediaPlayer!!.setDataSource(this, mUri)
                    mediaPlayer!!.setVolume(volume,volume)
                    mediaPlayer!!.prepare()
                    Toast.makeText(this,"Музыка выбрана!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectImage(a: Int){
        val image = findViewById<ImageView>(R.id.typeAlarmOff)
        when (a) {
            1 -> {
                image.setImageDrawable(getDrawable(R.drawable.image6))
            }
            2 -> {
                image.setImageDrawable(getDrawable(R.drawable.image7))
            }
            3 -> {
                image.setImageDrawable(getDrawable(R.drawable.image8))
            }
        }
    }

    fun daily(view: View) {
        when(view.id){
            R.id.mon ->{
                if (repeatAlarm.contains(days[0])){
                    repeatAlarm.remove(days[0])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[0])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.tue ->{
                if (repeatAlarm.contains(days[1])){
                    repeatAlarm.remove(days[1])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[1])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.wend ->{
                if (repeatAlarm.contains(days[2])){
                    repeatAlarm.remove(days[2])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[2])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.thus ->{
                if (repeatAlarm.contains(days[3])){
                    repeatAlarm.remove(days[3])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[3])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.fri ->{
                if (repeatAlarm.contains(days[4])){
                    repeatAlarm.remove(days[4])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[4])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.sat ->{
                if (repeatAlarm.contains(days[5])){
                    repeatAlarm.remove(days[5])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[5])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.sun ->{

                if (repeatAlarm.contains(days[6])){
                    repeatAlarm.remove(days[6])
                    view.background = getDrawable(R.drawable.text_back)
                } else {
                    repeatAlarm.add(days[6])
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
        }
    }

    fun heavens(view: View) {
        when(view.id){
            R.id.easyType ->{
                if (heavy != "easy"){
                    heavy = "easy"
                    view.background = getDrawable(R.drawable.text_back2)
                    findViewById<TextView>(R.id.averageType).background = getDrawable(R.drawable.text_back)
                    findViewById<TextView>(R.id.heavyType).background = getDrawable(R.drawable.text_back)
                }
            }
            R.id.averageType ->{
                if (heavy != "average"){
                    heavy = "average"
                    view.background = getDrawable(R.drawable.text_back2)
                    findViewById<TextView>(R.id.easyType).background = getDrawable(R.drawable.text_back)
                    findViewById<TextView>(R.id.heavyType).background = getDrawable(R.drawable.text_back)
                }
            }
            R.id.heavyType ->{
                if (heavy != "heavy"){
                    heavy = "heavy"
                    view.background = getDrawable(R.drawable.text_back2)
                    findViewById<TextView>(R.id.easyType).background = getDrawable(R.drawable.text_back)
                    findViewById<TextView>(R.id.averageType).background = getDrawable(R.drawable.text_back)
                }
            }
        }
    }

    fun typeRingtone(view: View) {
        when(view.id){
            R.id.vibrition ->{
                if (ringtoneType.contains("vibration") && !ringtoneType.contains("ringtone")){
                    Toast.makeText(this,"вы должны выбрать одно из функции", Toast.LENGTH_SHORT).show()
                } else if (ringtoneType.contains("vibration") && ringtoneType.contains("ringtone")) {
                    ringtoneType.remove("vibration")
                    view.background = getDrawable(R.drawable.text_back)
                } else{
                    ringtoneType.add("vibration")
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
            R.id.ringtone ->{
                if (ringtoneType.contains("ringtone") && !ringtoneType.contains("vibration")){
                    Toast.makeText(this,"вы должны выбрать одно из функции", Toast.LENGTH_SHORT).show()
                } else if (ringtoneType.contains("vibration") && ringtoneType.contains("ringtone")) {
                    ringtoneType.remove("ringtone")
                    view.background = getDrawable(R.drawable.text_back)
                } else{
                    ringtoneType.add("ringtone")
                    view.background = getDrawable(R.drawable.text_back2)
                }
            }
        }
    }

    private fun scheduleAlarm(
        mon: Boolean,
        tue: Boolean,
        wed: Boolean,
        thus: Boolean,
        fri: Boolean,
        sat: Boolean,
        sun: Boolean,
    ) {
        val builder = StringBuilder()
        for (x in ringtoneType){
            builder.append(x)
            builder.append("+")
        }

        val df: DateFormat = SimpleDateFormat("hh:mm a")
        val date: String = df.format(Calendar.getInstance().time)
        var alarmId = 1
        if ( alarms!!.size >= 0)
            alarmId =  alarms!!.size + 1
        //var alarmId = Random().nextInt(Int.MAX_VALUE)
        val alarm = Alarm(
            alarmId,
            hour,
            minute,
            true,
            recurring,
            mon,
            tue,
            wed,
            thus,
            fri,
            sat,
            sun,
            name,
            typeOff,
            heavy,
            ringtone,
            builder.toString(),
            volume,
            System.currentTimeMillis()
        )
        println(hour)
        println(minute)

        createAlarm.insert(alarm)
        alarm.schedule(applicationContext)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mediaPlayer != null){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.getContentResolver().query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun SaveAlarm(view: View) {
        name = if (nameText.text.toString() != ""){
            nameText.text.toString()
        } else
            "Будильник 1"

        var mon = false
        var tues = false
        var wed = false
        var thus = false
        var fri = false
        var sat = false
        var sun = false

        for (day in repeatAlarm){
            if (day == "Monday")
                mon = true
            if (day == "Tuesday")
                tues = true
            if (day == "Wednesday")
                wed = true
            if (day == "Thursday")
                thus = true
            if (day == "Friday")
                fri = true
            if (day == "Saturday")
                sat = true
            if (day == "Sunday")
                sun = true
        }
        recurring = mon || tues || wed || thus ||  fri || sat || sun

        scheduleAlarm(mon,tues,wed,thus,fri,sat,sun)

        //Toast.makeText(this,"Будильник сохранен!",Toast.LENGTH_SHORT).show()
        onBackPressed()
    }
    fun backHome(view: View) {
        onBackPressed()
    }
}