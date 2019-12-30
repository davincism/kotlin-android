package com.example.flashlight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //anko 사용
        /*flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startService(intentFor<TorchService>().setAction("on"))
            } else {
                startService(intentFor<TorchService>().setAction("off"))
            }
        }*/

        //일반
        val intent = Intent(this, TorchService::class.java)
        flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                intent.action="on"
            } else {
                intent.action="off"
            }
            startService(intent)
        }
    }
}
