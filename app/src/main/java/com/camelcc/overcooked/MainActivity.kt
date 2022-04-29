package com.camelcc.overcooked

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginRepository.initialize(applicationContext)
        setContentView(R.layout.activity_main)
    }
}