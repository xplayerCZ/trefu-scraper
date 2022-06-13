package model

import kotlinx.serialization.Serializable

@Serializable
class RouteStop(
    val stopId: Int,
    val routeId: Int,
    val index: Int,
    val served: Boolean
)