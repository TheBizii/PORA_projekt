package me.pora_project.main

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import me.pora_project.main.databinding.FragmentMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapFragment : Fragment() {
  private var _binding: FragmentMapBinding? = null
  private val binding get() = _binding!!
  private var _map: MapView? = null
  private val map get() = _map!!
  private var _mapController: IMapController? = null
  private val mapController get() = _mapController!!
  private var activityResultLauncher: ActivityResultLauncher<Array<String>>
  private lateinit var fusedLocationClient: FusedLocationProviderClient //https://developer.android.com/training/location/retrieve-current
  private var lastLocation: Location? = null
  private var locationCallback: LocationCallback
  private var locationRequest: com.google.android.gms.location.LocationRequest = com.google.android.gms.location.LocationRequest.create()
    .apply {
      interval = 1000
      fastestInterval = 500
      smallestDisplacement = 10f
      priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
      maxWaitTime = 1000
    }
  private var requestingLocationUpdates = false
  var startPoint: GeoPoint = GeoPoint(46.55951, 15.63970);
  var marker: Marker? = null


  companion object {
    val REQUEST_CHECK_SETTINGS = 20202
  }


  init {
    locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        for (location in locationResult.locations) {
          // Update UI with location data
          updateLocation(location) //MY function
        }
      }
    }

    this.activityResultLauncher = registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
      var allAreGranted = true
      for (b in result.values) {
        allAreGranted = allAreGranted && b
      }

      if (allAreGranted) {
        initCheckLocationSettings()
        //initMap() if settings are ok
      }
    }
  }


  // Binding object instance corresponding to the fragment_flavor.xml layout
  // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
  // when the view hierarchy is attached to the fragment.

  // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
  private val sharedViewModel: DataViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    val fragmentBinding = FragmentMapBinding.inflate(inflater, container, false)
    _binding = fragmentBinding

    val permission = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, INTERNET)
    ActivityCompat.requestPermissions(requireActivity(), permission, 0)

    Configuration.getInstance()
      .load(context, this.context?.getSharedPreferences("me.pora_project.preferences", Context.MODE_PRIVATE))


    _map = binding.mapView
    map.setTileSource(TileSourceFactory.MAPNIK)
    map.isClickable = true
    map.setMultiTouchControls(true)
    map.setUseDataConnection(true)

    _mapController = map.controller
    mapController.setZoom(16.0)
    mapController.setCenter(GeoPoint(33.989820, -81.029123))

    activityResultLauncher.launch(permission)

    return fragmentBinding.root
  }


  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    if (requestingLocationUpdates) {
      requestingLocationUpdates = false
      stopLocationUpdates()
    }
    binding.mapView.onPause()
  }

  private fun stopLocationUpdates() { //onPause
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.apply {
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
    _binding = null
  }

  fun updateLocation(newLocation: Location) {
    lastLocation = newLocation
    sharedViewModel.lastLocation = newLocation
    //GUI, MAP TODO
    startPoint.longitude = newLocation.longitude
    startPoint.latitude = newLocation.latitude
    mapController.setCenter(startPoint)
    getPositionMarker().position = startPoint
    map.invalidate()

  }

  private fun getPositionMarker(): Marker { //Singelton
    if (marker == null) {
      marker = Marker(map)
      marker!!.title = "Here I am"
      marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
      marker!!.icon =
        context?.let { ContextCompat.getDrawable(it, R.drawable.ic_outline_info_24) };
      map.overlays.add(marker)
    }
    return marker!!
  }

  fun initCheckLocationSettings() {
    val builder = LocationSettingsRequest.Builder()
      .addLocationRequest(locationRequest)
    val client: SettingsClient? = context?.let { LocationServices.getSettingsClient(it) }
    val task: Task<LocationSettingsResponse> = client!!.checkLocationSettings(builder.build())
    task.addOnSuccessListener { locationSettingsResponse ->
      Log.e("me.profek10.sporttrails","Settings Location IS OK")
      //MyEventLocationSettingsChange.globalState = true //default
      initMap()
      // All location settings are satisfied. The client can initialize
      // location requests here.
      // ...
    }

    task.addOnFailureListener { exception ->
      if (exception is ResolvableApiException) {
        // Location settings are not satisfied, but this can be fixed
        // by showing the user a dialog.
        Log.e("me.profek10.sporttrails","Settings Location addOnFailureListener call settings")
        try {
          // Show the dialog by calling startResolutionForResult(),
          // and check the result in onActivityResult().
          activity?.let {
            exception.startResolutionForResult(
              it,
              REQUEST_CHECK_SETTINGS
            )
          }
        } catch (sendEx: IntentSender.SendIntentException) {
          // Ignore the error.
          Log.e("me.profek10.sporttrails","Settings Location sendEx??")
        }
      }
    }

  }

  private fun initMap() {
    initLoaction()
    if (!requestingLocationUpdates) {
      requestingLocationUpdates = true
      startLocationUpdates()
    }
    mapController.setZoom(18.5)
    mapController.setCenter(startPoint);
    map.invalidate()
  }

  private fun initLoaction() { //call in create
    fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!
    readLastKnownLocation()
  }

  @SuppressLint("MissingPermission") //permission are checked before
  fun readLastKnownLocation() {
    fusedLocationClient.lastLocation
      .addOnSuccessListener { location: Location? ->
        location?.let { updateLocation(it) }
      }
  }

  @SuppressLint("MissingPermission")
  private fun startLocationUpdates() { //onResume
    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      locationCallback,
      Looper.getMainLooper()
    )
  }
}