package model

import util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

class RawConnection(
    val number: Int,
    val departureTimes: List<String>,
    var notes: String
)

@Serializable
class NewConnection(
    val routeId: Int,
    val number: Int,
    val departureTimes: List<@Serializable(with = LocalTimeSerializer::class) LocalTime?>,
    val ruleIds: List<Int>
)

