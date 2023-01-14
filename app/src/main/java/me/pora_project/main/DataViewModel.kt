package me.pora_project.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DataViewModel: ViewModel() {
  private val _quantity = MutableLiveData<Int>()
  val quantity: LiveData<Int> = _quantity

  private val _flavor = MutableLiveData<String>()
  val flavor: LiveData<String> = _flavor

  val dateOptions: List<String> = getPickupOptions()

  private val _date = MutableLiveData<String>()
  val date: LiveData<String> = _date

  private val _price = MutableLiveData<Double>()
  val price: LiveData<String> = Transformations.map(_price) {
    // Format the price into the local currency and return this as LiveData<String>
    NumberFormat.getCurrencyInstance().format(it)
  }

  init {
    resetData()
  }

  fun setQuantity(numberCupcakes: Int) {
    _quantity.value = numberCupcakes
    updatePrice()
  }

  fun setFlavor(desiredFlavor: String) {
    _flavor.value = desiredFlavor
  }

  fun setDate(pickupDate: String) {
    _date.value = pickupDate
    updatePrice()
  }

  fun hasNoFlavorSet(): Boolean {
    return _flavor.value.isNullOrEmpty()
  }

  fun resetData() {
    _quantity.value = 0
    _flavor.value = ""
    _date.value = dateOptions[0]
    _price.value = 0.0
  }

  private fun updatePrice() {
    var calculatedPrice: Double = ((quantity.value ?: 0) * 2).toDouble()
    // If the user selected the first option (today) for pickup, add the surcharge
    if (dateOptions[0] == _date.value) {
      calculatedPrice += 10
    }
    _price.value = calculatedPrice
  }

  private fun getPickupOptions(): List<String> {
    val options = mutableListOf<String>()
    val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
    val calendar = Calendar.getInstance()
    repeat(4) {
      options.add(formatter.format(calendar.time))
      calendar.add(Calendar.DATE, 1)
    }
    return options
  }
}