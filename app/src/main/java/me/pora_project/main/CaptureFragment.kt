package me.pora_project.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.material.slider.Slider
import me.pora_project.main.databinding.FragmentSettingsBinding


/**
 * [CaptureFragment] Settings screen to set interval for inputs
 */
class CaptureFragment : Fragment() {
  private val CAMERA_SLIDER = "CAMERA_SLIDER"
  private val AUDIO_AMPLITUDE = "AUDIO_AMPLITUDE"
  private val AUDIO_SCOPE = "AUDIO_SCOPE"
  private val GYROSCOPE = "GYROSCOPE"


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
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = Navigation.findNavController(view);

    binding!!.buttonSave.setOnClickListener {
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