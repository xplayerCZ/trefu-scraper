package model

import cz.davidkurzica.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class CallbackPacket(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean
)

@Serializable
class NewPacket(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean
)

data class RawPacket(
    val id: Int,
    val from: LocalDate,
    val to: LocalDate,
    val valid: Boolean
)

