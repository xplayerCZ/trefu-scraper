package model

import kotlinx.serialization.Serializable

@Serializable
class NewRoute(
    val id: Int,
    val length: Int,
    val direction: Int,
    val stopIds: List<Int>,
    val servedStopsIds:  List<Int>,
    val lineFullCode: Int
)