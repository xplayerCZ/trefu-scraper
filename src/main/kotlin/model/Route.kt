package model

import kotlinx.serialization.Serializable


@Serializable
class NewRoute(
    val lineId: Int,
    val length: Int,
    val direction: Int
)

@Serializable
class CallbackRoute(
    val id: Int,
    val lineId: Int,
    val length: Int,
    val direction: Int
)
