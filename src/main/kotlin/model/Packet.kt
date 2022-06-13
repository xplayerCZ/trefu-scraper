package model

import kotlinx.serialization.Serializable
import serializer.out.LocalDateSerializer
import java.time.LocalDate

data class RawPacket(
    val from: LocalDate,
    val to: LocalDate,
    val valid: Boolean,
    val code: Int
)

@Serializable
class NewPacket(
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean,
    val code: Int
)

@Serializable
class CallbackPacket(
    val id: Int,
    val from: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val to: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val valid: Boolean,
    val code: Int
)


