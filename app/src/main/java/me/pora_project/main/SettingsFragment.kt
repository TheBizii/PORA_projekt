package me.pora_project.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.material.slider.Slider
import me.pora_project.main.databinding.FragmentSettingsBinding


/**
 * [SettingsFragment] Settings screen to set interval for inputs
 */
class SettingsFragment : Fragment() {
  private lateinit var bindingsArray: Array<Slider>
  private lateinit var list: MutableMap<String, Float>
  private val CAMERA_SLIDER = "CAMERA_SLIDER"
  private val AUDIO_SLIDER = "AUDIO_SLIDER"
  private val GYROSCOPE_SLIDER = "GYROSCOPE_SLIDER"
  private lateinit var preferences: GetSharedPreferences

  // Binding object instance corresponding to the fragment_flavor.xml layout
  // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
  // when the view hierarchy is attached to the fragment.
  private var binding: FragmentSettingsBinding? = null

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
    binding = fragmentBinding
    preferences = GetSharedPreferences(context)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = Navigation.findNavController(view);

    bindingsArray = arrayOf(
      binding!!.cameraSlider,
      binding!!.audioSlider,
      binding!!.gyroscopeSlider
    )

    list = mutableMapOf(
      CAMERA_SLIDER to binding!!.cameraSlider.value,
      AUDIO_SLIDER to binding!!.audioSlider.value,
      GYROSCOPE_SLIDER to binding!!.gyroscopeSlider.value,
    )

    val out = preferences.show(list.keys);
    println(out)

    for (i in bindingsArray.indices){
      bindingsArray[i].value = out[i]
    }

      binding!!.buttonSave.setOnClickListener {
        list = mutableMapOf(
          CAMERA_SLIDER to binding!!.cameraSlider.value,
          AUDIO_SLIDER to binding!!.audioSlider.value,
          GYROSCOPE_SLIDER to binding!!.gyroscopeSlider.value,
        )
        preferences.save(list)
        navController.popBackStack()
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

  /**
   * This fragment lifecycle method is called when the view hierarchy associated with the fragment
   * is being removed. As a result, clear out the binding object.
   */
  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}