package com.example.smarthome.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.smarthome.R
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up client
        mqttClient = MqttAndroidClient(this, MQTT_BROKER_IP, "smarthome")

        mqttClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i(TAG, "MQTT Connected")
                Toast.makeText(applicationContext, "MQTT Connected, subscribing...", Toast.LENGTH_SHORT).show()
                sub("device_1")
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

    fun sub(name:String){
        mqttClient.subscribe("smarthome/${name}", 0) // 0 - is the QoS value
    }


    override fun onDestroy() {
        mqttClient.disconnect()
        super.onDestroy()
    }
}