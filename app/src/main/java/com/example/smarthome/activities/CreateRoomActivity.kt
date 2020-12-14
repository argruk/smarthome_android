package com.example.smarthome.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.smarthome.R
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_room_activity.*


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

        val languages = resources.getStringArray(R.array.icons)
        val spinner = icon_spinner
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, languages
            )
            spinner.adapter = adapter
        }


        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val db = FirebaseFirestore.getInstance()
        var new_id = db.collection("rooms").document().id

        add_room.setOnClickListener {
            var name = room_name.text
            var icon = icon_spinner.selectedItem.toString()

            if (name?.isNotEmpty()?.and(icon?.isNotEmpty()!!)!!) {

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

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.extra_menu, menu)
//        return super.onCreateOptionsMenu(menu);
//    }

}