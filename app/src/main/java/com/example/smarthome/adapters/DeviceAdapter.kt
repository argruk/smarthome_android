package com.example.smarthome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome.R
import com.example.smarthome.activities.DetailActivity
import com.example.smarthome.entities.Device
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.device_list_item.view.*
import org.eclipse.paho.client.mqttv3.MqttMessage


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
            // I know it is very hacky, and shouldn't be this way, but we're really short on time
            var active = (context as DetailActivity).findViewById<TextView>(R.id.active_textView)
            var inactive = (context as DetailActivity).findViewById<TextView>(R.id.inactive_textView)

            if(isChecked){
                updateDeviceState(data.id, true)
                active.text = (active.text.toString().toInt()+1).toString()
                inactive.text = (inactive.text.toString().toInt()-1).toString()

            }else{
                updateDeviceState(data.id, false)
                active.text = (active.text.toString().toInt()-1).toString()
                inactive.text = (inactive.text.toString().toInt()+1).toString()
            }

            // Here needs to send message to mqtt (a bit hacky)
            // We may want to send some more stuff, but that is for later. These methods should also be broken down
            // if we want to add more devices, etc. (to make more logic than just on/off)

            if(context is DetailActivity){
                var newMsg = MqttMessage()
                newMsg.payload = "${data.pinNumber}:${if (isChecked) "unlock" else "lock"}".toByteArray()
                (context as DetailActivity).mqttClient.publish("smarthome/devices", newMsg)
            }
        }
    }

    private fun updateDeviceState(id: String?, state: Boolean) {
        val db = FirebaseFirestore.getInstance()
        db.collection("devices").document(id.toString()).update("state", state)
            .addOnCompleteListener {
                Toast.makeText(context, R.string.state_hasbeen_updated, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, R.string.state_doesnt_updated, Toast.LENGTH_SHORT).show()
            }
    }

}