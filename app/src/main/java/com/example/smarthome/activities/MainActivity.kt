package com.example.smarthome.activities

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthome.R
import com.example.smarthome.adapters.RoomAdapter
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolbarHelper() {

    val roomsList = mutableListOf<RoomEntity>()
    val db = FirebaseFirestore.getInstance()
    val myAdapter = RoomAdapter(roomsList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()
        myAdapter.notifyDataSetChanged()
        all_devices.layoutManager = LinearLayoutManager(this)
        all_devices.adapter = myAdapter
    }

    private fun loadData() {
        db.collection("rooms")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var id = document.get("id").toString()
                    var title = document.get("title").toString()
                    var icon = document.get("icon").toString()
                    var roomObj = RoomEntity(id, title, icon)
                    Log.i("Rooms", "${roomObj}")

                    roomsList.add(roomObj)
                    myAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FireStore", "Error getting documents: ", exception)
            }
    }
}