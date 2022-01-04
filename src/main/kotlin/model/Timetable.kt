package model

data class RawTimetable(
    val connections: List<RawConnection>,
    val enabledStopsIndexes: List<Int>
)

data class Timetable(
    val connections: List<NewConnection> = emptyList(),
)