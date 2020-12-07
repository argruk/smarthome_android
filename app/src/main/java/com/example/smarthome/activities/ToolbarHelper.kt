package com.example.smarthome.activities

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.R

abstract class ToolbarHelper: AppCompatActivity() {

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