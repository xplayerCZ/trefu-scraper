package model

import kotlinx.serialization.Serializable

@Serializable
class CallbackRoute(
    val id: Int,
    val length: Int,
    val direction: Int,
    val stops: List<CallbackStop>,
)

@Serializable
class NewRoute(
    val direction: Int,
    val stopIds: List<Int>,
    val servedStopsIds:  List<Int>,
    val lineFullCode: Int
)