package model

import kotlinx.serialization.Serializable

data class Timetable(
    val connections: List<Connection>,
    val weekDays: Boolean,
    var lineFullCode: Int? = null,
    var stopIds: List<Int>? = null,
    val valid: Boolean = true
)

@Serializable
data class TimetableDTO(
    val packetId: Int,
    val lineId: Int,
    val duringWeekDay: Boolean,
    val stopIds: List<Int>,
    val connections: List<ConnectionDTO>,
    val valid: Boolean
)
