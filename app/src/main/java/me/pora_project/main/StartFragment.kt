package me.pora_project.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import me.pora_project.main.databinding.FragmentStartBinding

/**
 * This is the first screen of the Cupcake app. The user can choose how many cupcakes to order. When a button is clicked, it sets the quantity value in the shared view model and then navigates to the next fragment by <code>findNavController().navigate(R.id.action_startFragment_to_flavorFragment)</code>, where R.id.action_startFragment_to_flavorFragment is the action id defined in nav_graph.xml
 */
class StartFragment : Fragment() {

  // Binding object instance corresponding to the fragment_start.xml layout
  // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
  // when the view hierarchy is attached to the fragment.
  private var binding: FragmentStartBinding? = null

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
    binding = fragmentBinding
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val navController = Navigation.findNavController(view);
    binding!!.openInput.setOnClickListener {
      navController.navigate(R.id.action_startFragment_to_inputFragment);
    }
    binding!!.viewMap.setOnClickListener {
      navController.navigate(R.id.action_startFragment_to_mapFragment);
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