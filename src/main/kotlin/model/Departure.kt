package model

import kotlinx.serialization.Serializable
import serializer.out.LocalTimeSerializer
import java.time.LocalTime

@Serializable
class CallbackDeparture(
    val id: Int,
    val connectionId: Int,
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val index: Int
)

@Serializable
class NewDeparture(
    val connectionId: Int,
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val index: Int
)