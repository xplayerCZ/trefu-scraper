package model

import kotlinx.serialization.Serializable

data class Timetable(
    val connections: List<Connection>,
    val weekDays: Boolean,
    var lineFullCode: Int? = null,
    var valid: Boolean = true,
    var direction: Int? = null
)

@Serializable
data class TimetableDTO(
    val packetId: Int,
    val lineId: Int,
    val duringWeekDay: Boolean,
    val connections: List<ConnectionDTO>,
    val valid: Boolean,
    val direction: Int
)
