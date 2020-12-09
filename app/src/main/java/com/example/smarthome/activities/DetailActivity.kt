package com.example.smarthome.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthome.R
import com.example.smarthome.adapters.DeviceAdapter
import com.example.smarthome.adapters.RoomAdapter
import com.example.smarthome.entities.Device
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.room_layout_activity.*
import kotlinx.android.synthetic.main.room_layout_activity.all_rooms
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class DetailActivity : ToolbarHelper() {
    private val MQTT_BROKER_IP = "tcp://10.0.2.2:1883"
    // I mean come on, this is a useful comment
    // Note: If you want to use emulator, and access a broker running on the emulators host (your laptop),
    // then use the IP: 10.0.2.2 - it refers to host machine from Android emulator
    // In other cases, you will have to specify a 'real' ip. See
    //https://developer.android.com/studio/run/emulator-networking.html for more

    val TAG = "MQTT_COMMUNICATION"
    lateinit var mqttClient: MqttAndroidClient
    lateinit var roomId: String
    val db = FirebaseFirestore.getInstance()
    var devicesList = mutableListOf<Device>()
    val myAdapter = DeviceAdapter(devicesList, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_layout_activity)

        roomId = intent.getStringExtra("roomId").toString()



//        setUpMqtt()

        setUpLayout()
        all_rooms.layoutManager = LinearLayoutManager(this)
        all_rooms.adapter = myAdapter

        turn_off_all.setOnClickListener{
            for(d in devicesList){
                db.collection("devices")
                    .document(d.id.toString())
                    .update("state", false)
            }
            devicesList.clear()
            setUpDeviceStates()
            Toast.makeText(this, "All devices switched off", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setUpLayout() {
        val docRef = roomId?.let { db.collection("rooms").document(it) }
        docRef.get()
            .addOnSuccessListener { document ->
                var roomObj = RoomEntity(document.get("id").toString(), document.get("title").toString(), document.get("icon").toString())
                roomName.text = roomObj.title

                val id: Int = this.resources.getIdentifier(roomObj.icon.toString(), "drawable", this.packageName)
                roomImage.setImageResource(id)

                setUpDeviceStates()

            }
    }

    private fun setUpDeviceStates() {
        db.collection("devices")
            .whereEqualTo("room_id", roomId)
            .get()
            .addOnSuccessListener { documents ->

                var active = 0
                var deactive = 0

                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var deviceObj = Device(document.get("id").toString(), document.get("title").toString(), document.get("room_id").toString(), document.get("state").toString().toBoolean(), document.get("pinNumber").toString().toInt())
                    devicesList.add(deviceObj)
                    myAdapter.notifyDataSetChanged()

                    var flag: Boolean = document.get("state").toString().toBoolean()
                    if(flag){
                        active += 1
                    }else{
                        deactive += 1
                    }
                }

                active_textView.text = active.toString()
                inactive_textView.text = deactive.toString()


            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setUpMqtt() {
        // set up client
        mqttClient = MqttAndroidClient(this, MQTT_BROKER_IP, "smarthome")

        mqttClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i(TAG, "MQTT Connected")
                Toast.makeText(applicationContext, "MQTT Connected, subscribing...", Toast.LENGTH_SHORT).show()
                mqttClient.subscribe("smarthome/<here_we_will_place_device_id_i_guess>", 0) // 0 - is the QoS value
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.i(TAG, "MQTT Message: $topic, msg: ${message.toString()}")
                // do nothing
            }

            override fun connectionLost(cause: Throwable?) {
                Log.i(TAG, "MQTT Connection Lost!")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.i(TAG, "MQTT Message Delivered!")
            }
        })

        mqttClient.connect( MqttConnectOptions().apply { isAutomaticReconnect=true })    //  Can also specify options, e.g. :  mqttClient.connect( MqttConnectOptions().apply { isAutomaticReconnect=true } )

    }

    override fun onDestroy() {
//        mqttClient.disconnect()
        super.onDestroy()
    }
}