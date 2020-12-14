package com.example.smarthome.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.smarthome.R
import com.example.smarthome.entities.Device
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_device_activity.*


class CreateActivity : ToolbarHelper() {

    lateinit var roomId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_device_activity)

        add_device.setOnClickListener {
            val title = device_name.text.toString()
            var pinNumber = pinNumber.text.toString()

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
                            R.string.device_hasbeen_added,
                            Toast.LENGTH_SHORT
                        ).show()
                        var intent = Intent(this,DetailActivity::class.java)
                        intent.putExtra("roomId",roomId)
                        startActivity(intent)
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(this, R.string.device_dont_added, Toast.LENGTH_SHORT)
                            .show()
                    }
            }else{
                Toast.makeText(this, R.string.fillin_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

}