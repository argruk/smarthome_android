package com.example.smarthome.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.smarthome.R
import com.example.smarthome.entities.Device
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.add_device_activity.*


class CreateActivity : ToolbarHelper() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_device_activity)
        setUpFileSpinner()

        add_device.setOnClickListener{
            val title = device_name.text.toString()
            val state = false
            val room = room_spinner.selectedItem
            var roomId = ""
            val db = FirebaseFirestore.getInstance()
            var new_id = db.collection("rooms").document().id



            db.collection("rooms")
                .whereEqualTo("title", room)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        roomId = document.data.get("id").toString()
                    }

                    val device =  Device(new_id, title, roomId, state)

                    db.collection("devices").document(new_id).set(device)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Device has been added successfully", Toast.LENGTH_SHORT).show()
                            startMainActivity()
                        }
                        .addOnFailureListener{
                                e -> Toast.makeText(this, "Device has not been added!", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w("FireStore", "Error getting documents: ", exception)
                }


        }
    }

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

    private fun setUpFileSpinner() {
        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("rooms")
        val spinner = findViewById<Spinner>(R.id.room_spinner)
        val rooms: MutableList<String?> = ArrayList()
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            rooms
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        subjectsRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("title")
                    rooms.add(subject)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

}