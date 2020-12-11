package com.example.smarthome.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarthome.R
import com.example.smarthome.adapters.DeviceAdapter
import com.example.smarthome.entities.Device
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.room_layout_activity.*
import kotlinx.android.synthetic.main.room_layout_activity.all_devices
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class DetailActivity : ToolbarHelper() {
    /*
    * TODO: We need "Back" button, since after we add a device, you cannot go back.
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    *
    * TODO: Test newer MQTT and rework the communication, so MQTT can parse messages correctly
    * */

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

        setUpLayout()
        all_devices.layoutManager = LinearLayoutManager(this)
        all_devices.adapter = myAdapter

        turn_off_all.setOnClickListener{
            for(d in devicesList){
                db.collection("devices")
                    .document(d.id.toString())
                    .update("state", false)
            }
            devicesList.clear()
            setUpDeviceStates()
            Toast.makeText(this, R.string.all_devices_turned_off, Toast.LENGTH_SHORT).show()
        }

        setUpMqtt()
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

        new_device.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra("room_id",roomId)
            startActivity(intent)
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

                    var deviceObj = Device(document.get("id").toString(), document.get("title").toString(),
                        document.get("room_id").toString(), document.get("state").toString().toBoolean(),
                        document.get("pinNumber").toString())

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
                Toast.makeText(applicationContext, R.string.mqtt_connected, Toast.LENGTH_SHORT).show()
                sub("device_1")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.i(TAG, "MQTT Message: $topic, msg: ${message.toString()}")
                // do nothing
                // Technically, we wouldn't receive messages, but rather send them
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

    fun sub(name:String){
        mqttClient.subscribe("smarthome/${name}", 0) // 0 - is the QoS value
    }

    override fun onDestroy() {
        mqttClient.disconnect()
        super.onDestroy()
    }
}