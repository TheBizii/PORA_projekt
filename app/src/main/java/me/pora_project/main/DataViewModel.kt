package me.pora_project.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DataViewModel: ViewModel() {
  var lastLocation: Location? = null

  init {
    resetData()
  }

  fun resetData() {
    lastLocation = null
  }
}