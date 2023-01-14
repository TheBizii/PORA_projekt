package me.pora_project.lib

import java.util.*

data class Place(
  var name: String,
  var height: Int,
  var type: MutableList<PlaceTypeEnum> = arrayListOf(PlaceTypeEnum.SUMMIT),
  var country: String,
  var mountainRange: String?
) : Comparable<Place> {
  private val uuid = UUID.randomUUID()
  override fun compareTo(other: Place) = height.compareTo(other.height)

  override fun toString(): String {
    return "Place(name='$name', height=$height, type=$type, country='$country', mountainRange=$mountainRange, uuid=$uuid)"
  }
}

enum class PlaceTypeEnum {
  START, SUMMIT, COTTAGE, MOUNTAIN, CHURCH, WATERFALL, BIVOUAC, RIDGE, LAKE, END
}
