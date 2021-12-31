package model

import util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

data class Connection(
    val number: Int,
    val departures: List<@Serializable(with = LocalTimeSerializer::class) LocalTime?>,
    val notes: String,
    val weekDays: Boolean,
    var lineFullCode: String? = null
)

@Serializable
data class ConnectionDTO(
    val number: Int,
    val notes: String,
)

