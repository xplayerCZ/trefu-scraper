package model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: Int,
    val name: String
)
