package model

import cz.davidkurzica.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

data class Packet(
    val id: Int,
    val from: LocalDate,
    val to: LocalDate,
    val valid: Boolean
)

@Serializable
data class PacketDTO(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean
)