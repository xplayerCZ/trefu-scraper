package model

import util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
class NewConnection(
    val id: Int,
    val routeId: Int,
    val number: Int,
    val departureTimes: List<@Serializable(with = LocalTimeSerializer::class) LocalTime>,
    val ruleIds: List<Int>
)

