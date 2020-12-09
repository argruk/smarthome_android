package com.example.smarthome.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome.R
import com.example.smarthome.activities.DetailActivity
import com.example.smarthome.entities.Device
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.device_list_item.view.*
import kotlinx.android.synthetic.main.room_list_item.view.*

class DeviceAdapter(var devices: List<Device>, var context: Context): RecyclerView.Adapter<RoomViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_list_item, parent, false)
        val viewHolder = RoomViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val data = devices[position]
        holder.item.deviceName.text = data.title
        holder.item.state.isChecked = data.state.toString().toBoolean()


        holder.item.state.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                updateDeviceState(data.id, true)
            }else{
                updateDeviceState(data.id, false)
            }
        }

    }

    private fun updateDeviceState(id: String?, state: Boolean) {
        val db = FirebaseFirestore.getInstance()
        db.collection("devices").document(id.toString()).update("state", state)
            .addOnCompleteListener {
                Toast.makeText(context, "State has been updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "State doesn't updated", Toast.LENGTH_SHORT).show()
            }
    }

}