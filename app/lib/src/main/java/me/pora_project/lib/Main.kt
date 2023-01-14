package me.pora_project.lib

//TODO add Location class to display to maps

object Main {
  private fun generate() {
    val places: MutableList<Place> = arrayListOf(
      Place("Zagrad", 240, arrayListOf(PlaceTypeEnum.START), "Slovenia", null), Place(
        "Celjska koča",
        652,
        arrayListOf(PlaceTypeEnum.COTTAGE),
        "Slovenia",
        "Posavsko hribovje in Dolenjska",
      )
    )
    val paths = arrayListOf(
      Path(
        places[0], places[1], 70, IntensityEnum.MEDIUM, TransportTypeEnum.HIKE, null, null
      )
    )
    places.sort()
    paths.sort()
    println(places)
    println(paths)
  }

  @JvmStatic
  fun main(args: Array<String>) {
    generate()
  }
}