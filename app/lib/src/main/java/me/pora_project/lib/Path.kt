package me.pora_project.lib

data class Path(
  var start: Place,
  var end: Place,
  var duration: Int,
  var intensity: IntensityEnum = IntensityEnum.MEDIUM,
  var type: TransportTypeEnum = TransportTypeEnum.HIKE,
  var recommendedSummer: String?,
  var recommendedWinter: String?
) : Comparable<Path> {
  var heightDifference = kotlin.math.abs(end.height - start.height);
  override fun compareTo(other: Path) = duration.compareTo(other.duration)

  override fun toString(): String {
    return "Path(start=$start, end=$end, duration=$duration, intensity=$intensity, type=$type, recommendedSummer=$recommendedSummer, recommendedWinter=$recommendedWinter, heightDifference=$heightDifference)"
  }
}

enum class TransportTypeEnum {
  WALK, HIKE, CLIMB, BIKE
}

enum class IntensityEnum {
  EASY, MEDIUM, HARD, DEMANDING, INTENSE
}
