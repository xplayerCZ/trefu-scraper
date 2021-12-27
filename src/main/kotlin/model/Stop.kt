package model

import kotlinx.serialization.Serializable

data class Stop(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: String
)

@Serializable
data class StopDTO(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val code: Int
)