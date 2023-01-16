package me.pora_project.main

import android.location.Location
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {
  var lastLocation: Location? = null

  init {
    resetData()
  }

  fun resetData() {
    lastLocation = null
  }
}