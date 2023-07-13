package com.example.aplikasittd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TentangActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}