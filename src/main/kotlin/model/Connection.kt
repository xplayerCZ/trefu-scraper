package model

import kotlinx.serialization.Serializable
import java.time.LocalTime

class RawConnection(
    val number: Int,
    val departureTimes: List<LocalTime?>,
    var notes: String
)

@Serializable
class NewConnection(
    val routeId: Int,
    val number: Int,
)

@Serializable
class CallbackConnection(
    val id: Int,
    val routeId: Int,
    val number: Int,
)

