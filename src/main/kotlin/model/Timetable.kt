package model

data class Timetable(
    val connections: List<NewConnection> = emptyList(),
    val routes: List<NewRoute> = emptyList()
)

data class RawTimetable(
    val content: String
)