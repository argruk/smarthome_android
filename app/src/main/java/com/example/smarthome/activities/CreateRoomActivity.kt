package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smarthome.R
import com.example.smarthome.activities.ToolbarHelper
import com.example.smarthome.entities.RoomEntity
import kotlinx.android.synthetic.main.add_room_activity.*
import java.util.*
import kotlin.collections.HashMap


class CreateRoomActivity : ToolbarHelper() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_room_activity)

        val db = FirebaseFirestore.getInstance()
        var new_id = db.collection("rooms").document().id

        add_room.setOnClickListener{
            var name = room_name.text
            var icon = icon.text

            val room =  RoomEntity(new_id, name.toString(), icon.toString())

            db.collection("rooms").document(new_id).set(room)
                .addOnSuccessListener {
                    Toast.makeText(this, "Room has been added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                        e -> Toast.makeText(this, "Room has not been added!", Toast.LENGTH_SHORT).show()
                }
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