package com.example.smarthome.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smarthome.R
import com.example.smarthome.entities.RoomEntity
import kotlinx.android.synthetic.main.add_room_activity.*
import java.util.*
import kotlin.collections.HashMap


class CreateRoomActivity : ToolbarHelper() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    private fun startMainActivity() {


        mRunnable = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        mHandler = Handler()

        mHandler.postDelayed(mRunnable, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_room_activity)

        val db = FirebaseFirestore.getInstance()
        var new_id = db.collection("rooms").document().id

        add_room.setOnClickListener {
            var name = room_name.text
            var icon = icon.text

            if (name.isNotEmpty() and icon.isNotEmpty()) {

                val room = RoomEntity(new_id, name.toString(), icon.toString())

                db.collection("rooms").document(new_id).set(room)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.room_hasbeen_added, Toast.LENGTH_SHORT)
                            .show()
                        startMainActivity()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, R.string.room_didnt_added, Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, R.string.fillin_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.extra_menu, menu)
        return super.onCreateOptionsMenu(menu);
    }

}