package model

import kotlinx.serialization.Serializable

@Serializable
class ConnectionRule(
    val connectionId: Int,
    val ruleId: Int,
)