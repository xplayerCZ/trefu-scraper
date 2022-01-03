package model

import kotlinx.serialization.Serializable

@Serializable
class NewLine(
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int
)

data class RawLine(
    val fullCode: Int,
    val shortCode: String
)