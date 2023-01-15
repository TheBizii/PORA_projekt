package me.pora_project.main

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import me.pora_project.main.databinding.FragmentCaptureBinding
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.File


/**
[CaptureFragment] Settings screen to set interval for inputs
 */
class CaptureFragment : Fragment(), SensorEventListener {
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
  private var binding: FragmentCaptureBinding? = null
  private lateinit var mqttHelper: MqttHelper

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentCaptureBinding.inflate(inflater, container, false)
    binding = fragmentBinding
    preferences = GetSharedPreferences(context)
    binding!!.camera.setLifecycleOwner(this)
    mSensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager;
    mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    mqttHelper = MqttHelper(requireContext())
    startMqtt()
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = Navigation.findNavController(view);
    binding!!.camera.setLifecycleOwner(viewLifecycleOwner);

    list = mutableSetOf(
      CAMERA_SLIDER,
      AUDIO_SLIDER,
      GYROSCOPE_SLIDER,
    )

    val out = preferences.show(list);

    binding!!.buttonStart.setOnClickListener {
      loopback = true
      startIntervalCapturing(out)
      mSensorManager.registerListener(this, mGyroscope, out[2].toInt());
    }
    binding!!.buttonStop.setOnClickListener {
      loopback = false
      mSensorManager.unregisterListener(this);
    }
    binding!!.buttonExit.setOnClickListener {
      navController.popBackStack()
    }

    binding!!.camera.addCameraListener(object : CameraListener() {
      override fun onPictureTaken(result: PictureResult) {
        // Picture was taken!
        // If planning to show a Bitmap, we will take care of
        // EXIF rotation and background threading for you...
        //val maxWidth = 200
        //val maxHeight = 200

        /*result.toBitmap(maxWidth, maxHeight) {
          println("Callback")
        }*/

        // If planning to save a file on a background thread,
        // just use toFile. Ensure you have permissions.
        //result.toFile(File("file.jpg")) {
        //println("Callback")
        //}

        // Access the raw data if needed.
        val data = result.data
        mqttHelper.publish(MqttHelper.CAMERA_TOPIC, "a|b|$data")
      }
    })

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
    val runnableCamera: Runnable = object : Runnable {
      override fun run() {
        binding!!.camera.takePicture()
        if (loopback) {
          handler.postDelayed(this, out[0].toLong() * 100)
        }
      }
    }

    val runnableAudio: Runnable = object : Runnable {
      override fun run() {
        val recorder = MediaRecorder()
        try {
          recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
          recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
          val audioFile = File(context?.filesDir, "audio.3gp")
          println(audioFile.absolutePath)
          recorder.setOutputFile(audioFile.absolutePath)
          recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
          recorder.prepare()
          recorder.start()
          recorder.stop();
        } catch (e: Exception) {
          println(e)
        } finally {
          recorder.release()
          if (loopback) {
            handler.postDelayed(this, out[1].toLong() * 100)
          }
        }
      }
    }
    handler.post(runnableCamera)
    handler.post(runnableAudio)
  }

  /**
   * This fragment lifecycle method is called when the view hierarchy associated with the fragment
   * is being removed. As a result, clear out the binding object.
   */
  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
    mSensorManager.unregisterListener(this);
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
      val x: Float = event.values[0]
      val y: Float = event.values[1]
      val z: Float = event.values[2]
      //println("$x, $y, $z")
      // Do something with the gyroscope data
    }
  }

  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    //TODO("Not yet implemented")
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

