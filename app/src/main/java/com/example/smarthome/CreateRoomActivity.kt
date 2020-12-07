package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_room_activity.*

class CreateRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_room_activity)

        add_room.setOnClickListener{
            var name = add_room.text

        }
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
                Log.i("Clicked", "Add Room will be opened")
            }
            R.id.settings -> {
                Log.i("Clicked", "Settings will be opened")
            }
        }

        return true
    }

}