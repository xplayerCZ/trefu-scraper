package model

import kotlinx.serialization.Serializable

@Serializable
class NewStop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)

@Serializable
data class RawRouteStop(
    val name: String
)

data class RawStop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)