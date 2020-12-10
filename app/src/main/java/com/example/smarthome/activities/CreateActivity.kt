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

        add_device.setOnClickListener {
            val title = device_name.text.toString()
            var pinNumber = pinNumber.text.toString().toInt()

            if (title.isNotEmpty()) {
                val state = false
                var roomId = intent.getStringExtra("room_id")
                val db = FirebaseFirestore.getInstance()
                var new_id = db.collection("rooms").document().id


                val device = Device(new_id, title, roomId, state, pinNumber)

                db.collection("devices").document(new_id).set(device)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Device has been added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startMainActivity()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Device has not been added!", Toast.LENGTH_SHORT)
                            .show()
                    }


            }else{
                Toast.makeText(this, "Please fill in all inputs", Toast.LENGTH_SHORT).show()
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

}