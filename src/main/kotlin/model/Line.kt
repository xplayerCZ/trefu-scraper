package model

import kotlinx.serialization.Serializable

data class Line(
    val fullCode: Int,
    val shortCode: String
)

@Serializable
data class LineDTO(
    val fullCode: Int,
    val shortCode: String
)
