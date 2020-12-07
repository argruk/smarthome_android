package com.example.smarthome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.extra_menu, menu)
        return super.onCreateOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.add_room -> {
                val intent = Intent(this, CreateRoomActivity::class.java)
                startActivity(intent)
            }
            R.id.add_device -> {
                val intent = Intent(this, CreateActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                Log.i("Clicked", "Settings will be opened")
            }
        }

        return true
    }
}