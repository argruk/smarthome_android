#include <ESP32Servo.h>
#include <WiFi.h>
#include <PubSubClient.h>
const char* ssid     = "FD-70";
const char* password = "wifi2018";
// To find the IP, in your computer (Windows), in command line type "ipconfig" and look at the "Wireless LAN adapter -> IPv4" address.
// If you have MacOS - google yourself :)
const char* mqtt_server = "192.168.43.14";

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
 
Servo myservo;  // create servo object to control a servo
// 16 servo objects can be created on the ESP32
 
int pos = 0;    // variable to store the servo position
// Recommended PWM GPIO pins on the ESP32 include 2,4,12-19,21-23,25-27,32-33 
int servoPin = 25;
bool isOpen = false;

void reconnect() {
  // Loop until we're reconnected
  while (!mqttClient.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (mqttClient.connect(clientId.c_str())) {
      Serial.println("Connected");
      mqttClient.subscribe("smarthome/devices");
      // Connected - do something useful - subscribe to topics, publish messages, etc.
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void lock(){
  for (pos = pos; pos <= 180; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    myservo.write(pos);    // tell servo to go to position in variable 'pos'
    delay(15);             // waits 15ms for the servo to reach the position
    isOpen = false;
    Serial.println(isOpen);
  }
}

void unlock(){
  for (pos = 180; pos >= 0; pos -= 1) { // goes from 180 degrees to 0 degrees
    myservo.write(pos);    // tell servo to go to position in variable 'pos'
    delay(15);             // waits 15ms for the servo to reach the position
    isOpen = true;
    Serial.println(isOpen);
}
}

void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();

  // If a message is received on the topic smarthome/device1, you check if the message is either "lock" or "unlock". 
  // Changes the function execution state according to the message
  if (String(topic) == "smarthome/devices") {
    if(messageTemp == "device1:lock"){
      Serial.println("lock");
      lock();
    }
    else if(messageTemp == "device1:unlock"){
      Serial.println("unlock");
      unlock();
    }else{
      Serial.println(messageTemp);
    }
  }
}
 
void setup() {
  Serial.begin(115200);
  delay(15);
  // Connecting to a WiFi network
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    // Set up MQTT
    mqttClient.setServer(mqtt_server, 1883);
    mqttClient.subscribe("smarthome/devices");
    mqttClient.setCallback(callback);
    
  
  
  // Allow allocation of all timers
  ESP32PWM::allocateTimer(0);
  ESP32PWM::allocateTimer(1);
  ESP32PWM::allocateTimer(2);
  ESP32PWM::allocateTimer(3);
  myservo.setPeriodHertz(50); 
  myservo.attach(servoPin, 500, 2400); 
}

void loop() {
if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop();
  delay(1000); 
  
}
