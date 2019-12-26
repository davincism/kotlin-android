package com.example.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var tiltView: TiltView

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //화면 안 꺼지게
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE //화면 가로모드 고정
        super.onCreate(savedInstanceState)

        tiltView = TiltView(this)
        setContentView(tiltView)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // 센서값이 변경되면 호출됨
        // values[0]: x축값 : 위로 기울이면 -10~0, 아래로 기울이면 : 0~10
        // values[1]: y축값 : 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 : 0~10
        // values[2]: z축값 : 미사용

        event?.let {
            Log.d(
                "MainActivity",
                "onSensorChanged: x :" + " ${event.values[0]}, y : ${event.values[1]}, z : ${event.values[2]}"
            )
            tiltView.onSensorEvent(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //센서 정밀도 변경시 호출됨
    }

    //이건 뭔가?
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
