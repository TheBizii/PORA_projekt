package me.pora_project.main

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import me.pora_project.main.databinding.FragmentSimulateBinding
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.LocalDateTime


/**
[SimulateFragment] Settings screen to set interval for inputs
 */
class SimulateFragment : Fragment() {
  private lateinit var time: String
  private lateinit var mSensorManager: SensorManager
  private lateinit var mGyroscope: Sensor
  private var loopback: Boolean = false
  private val CAMERA_SLIDER = "CAMERA_SLIDER"
  private val AUDIO_SLIDER = "AUDIO_SLIDER"
  private val GYROSCOPE_SLIDER = "GYROSCOPE_SLIDER"
  private lateinit var list: MutableSet<String>
  private lateinit var preferences: GetSharedPreferences


  // Binding object instance corresponding to the fragment_flavor.xml layout
  // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
  // when the view hierarchy is attached to the fragment.
  private var binding: FragmentSimulateBinding? = null
  private lateinit var mqttHelper: MqttHelper

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentSimulateBinding.inflate(inflater, container, false)
    binding = fragmentBinding
    preferences = GetSharedPreferences(context)

    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = Navigation.findNavController(view);

    mqttHelper = MqttHelper(requireContext())
    startMqtt()

    list = mutableSetOf(
      CAMERA_SLIDER,
      AUDIO_SLIDER,
      GYROSCOPE_SLIDER,
    )

    val out = preferences.show(list);
    time = LocalDateTime.now().toString()

    binding!!.buttonSimulate.setOnClickListener {
      loopback = true
      startIntervalCapturing(out)
    }
    binding!!.buttonStop.setOnClickListener {
      loopback = false
    }
    binding!!.buttonExit.setOnClickListener {
      navController.popBackStack()
    }

    binding?.apply {
      // Specify the fragment as the lifecycle owner
      // lifecycleOwner = viewLifecycleOwner

      // Assign the view model to a property in the binding class
      // viewModel = sharedViewModel

      // Assign the fragment
      // flavorFragment = this@FlavorFragment
    }
  }

  private fun startIntervalCapturing(out: MutableList<Float>) {
    val handler = Handler()
    val runnableSimulate: Runnable = object : Runnable {
      override fun run() {
        val data =
          (binding!!.editTextFrom.text.toString().toInt()..binding!!.editTextFrom.text.toString()
            .toInt()).random()
        mqttHelper.publish(
          MqttHelper.CAMERA_TOPIC,
          "$data|$time|${sharedViewModel.lastLocation?.latitude},${sharedViewModel.lastLocation?.longitude}"
        )
        if (loopback) {
          handler.postDelayed(this, out[0].toLong() * 100)
        }
      }
    }

    handler.post(runnableSimulate)
  }

  /**
   * This fragment lifecycle method is called when the view hierarchy associated with the fragment
   * is being removed. As a result, clear out the binding object.
   */
  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  private fun startMqtt() {
    mqttHelper.mqttClient.setCallback(object : MqttCallback {
      override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.d(MqttHelper.TAG, "Receive message: ${message.toString()} from topic: $topic")
        //binding.tvPayload.text = message.toString()
      }

      override fun connectionLost(cause: Throwable?) {
        Log.d(MqttHelper.TAG, "Connection lost ${cause.toString()}")
      }

      override fun deliveryComplete(token: IMqttDeliveryToken?) {

      }
    })
  }
}

