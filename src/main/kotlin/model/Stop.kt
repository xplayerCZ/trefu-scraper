package model

import kotlinx.serialization.Serializable


@Serializable
data class RawRouteStop(
    val name: String
)

data class RawStop(
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)

@Serializable
class NewStop(
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)

@Serializable
class CallbackStop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)
