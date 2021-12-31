package model

import kotlinx.serialization.Serializable
import util.LocalTimeSerializer
import java.time.LocalTime

@Serializable
data class DepartureDTO(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val connectionNumber: Int,
    val direction: Int,
    val lineFullCode: Int,
    val packetId: Int,
    val index: Int
)