package model

import kotlinx.serialization.Serializable


data class RawLine(
    val fullCode: Int,
    val shortCode: String
)

@Serializable
class NewLine(
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int
)

@Serializable
class CallbackLine(
    val id: Int,
    val shortCode: String,
    val fullCode: Int,
    val packetId: Int,
)
