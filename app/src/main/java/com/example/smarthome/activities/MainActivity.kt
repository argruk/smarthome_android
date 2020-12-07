package com.example.smarthome.activities

import android.os.Bundle
import android.view.Menu
import com.example.smarthome.R

class MainActivity : ToolbarHelper() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.extra_menu, menu)
        return super.onCreateOptionsMenu(menu);
    }

}