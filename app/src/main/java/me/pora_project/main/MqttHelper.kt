package me.pora_project.main

//import org.eclipse.paho.android.service.MqttAndroidClient
import android.content.Context
import android.util.Log
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

// code based on: https://www.emqx.com/en/blog/android-connects-mqtt-using-kotlin?utm_source=mqttx&utm_medium=referral&utm_campaign=mqttx-help-to-blog

/*
Test client:
MQTTX https://mqttx.app/

Free brokers:
---------------------------
EMQX - https://www.emqx.com/en
Broker: broker.emqx.io
TCP Port: 1883
Websocket Port: 8083
---------------------------
HiveMQ - http://www.mqtt-dashboard.com/
Broker: broker.hivemq.com
TCP Port: 1883
Websocket Port: 8000
TLS TCP Port: 8883
TLS Websocket Port: 8884
---------------------------
 */

class MqttHelper(context: Context) {

  var mqttClient: MqttAndroidClient

  companion object {
    val TAG = MqttHelper::class.simpleName
    const val SERVER_URI = BuildConfig.mqttServerUri;
    const val CAMERA_TOPIC = BuildConfig.mqttCamera
    const val AUDIO_TOPIC = BuildConfig.mqttAudio
    const val GYROSCOPE_TOPIC = BuildConfig.mqttGyroscope
    val clientId: String = MqttClient.generateClientId()
  }

  init {
    val options = MqttConnectOptions()
    options.userName = BuildConfig.mqttUser
    options.password = BuildConfig.mqttPassword.toCharArray()
    mqttClient = MqttAndroidClient(context, SERVER_URI, clientId)
    try {
      mqttClient.connect(options, null, object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
          Log.d(TAG, "Connection success")
          subscribe(CAMERA_TOPIC)
          subscribe(AUDIO_TOPIC)
          subscribe(GYROSCOPE_TOPIC)
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
          Log.e(TAG, exception?.message.toString())
          Log.e(TAG, exception?.stackTrace.contentToString())
          Log.d(TAG, "Connection failure")
        }
      })
    } catch (e: MqttException) {
      e.printStackTrace()
    }
  }

  fun subscribe(topic: String, qos: Int = 1) {
    try {
      if (mqttClient.isConnected) {
        mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
          override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d(TAG, "Subscribed to $topic")
          }

          override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d(TAG, "Failed to subscribe $topic")
          }
        })
      }
    } catch (e: MqttException) {
      e.printStackTrace()
    }
  }

  fun unsubscribe(topic: String) {
    try {
      mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
          Log.d(TAG, "Unsubscribed to $topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
          Log.d(TAG, "Failed to unsubscribe $topic")
        }
      })
    } catch (e: MqttException) {
      e.printStackTrace()
    }
  }

  fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
    try {
      val message = MqttMessage()
      message.payload = msg.toByteArray()
      message.qos = qos
      message.isRetained = retained
      if (mqttClient.isConnected) {
        mqttClient.publish(topic, message, null, object : IMqttActionListener {
          override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d(TAG, "$msg published to $topic")
          }

          override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d(TAG, "Failed to publish $msg to $topic")
          }
        })
      }
    } catch (e: MqttException) {
      e.printStackTrace()
    }
  }

  fun disconnect() {
    try {
      mqttClient.disconnect(null, object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
          Log.d(TAG, "Disconnected")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
          Log.d(TAG, "Failed to disconnect")
        }
      })
    } catch (e: MqttException) {
      e.printStackTrace()
    }
  }
}