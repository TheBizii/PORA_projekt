package me.pora_project.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import me.pora_project.main.databinding.FragmentInputBinding

/**
 * [InputFragment] Input data to sent to server on intervals
 */
class InputFragment : Fragment() {
  private lateinit var bindingsArray: Array<EditText>

  // Binding object instance corresponding to the fragment_flavor.xml layout
  // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
  // when the view hierarchy is attached to the fragment.
  private var binding: FragmentInputBinding? = null

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentInputBinding.inflate(inflater, container, false)
    binding = fragmentBinding
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = Navigation.findNavController(view);

    bindingsArray = arrayOf(
      binding!!.editTextDuration,
      binding!!.editTextSummer,
      binding!!.editTextWinter,
    )


    binding!!.buttonAdd.setOnClickListener {
      //TODO add data to dataViewModel
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