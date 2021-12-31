package model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val name: String
)

@Serializable
data class RouteDTO(
    val stopIds: List<Int>,
    val direction: Int,
    val lineFullCode: Int,
    val packetId: Int
)

