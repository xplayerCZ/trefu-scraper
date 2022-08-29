package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.ConnectionRule

@Serializable
@Resource("connection-rules")
class ConnectionRules(
    val connectionId: Int? = null,
    val ruleId: Int? = null,
)

suspend fun ReportManager.exists(connRule: ConnectionRule): ConnectionRule? {
    return client.get(
        ConnectionRules(
            connectionId = connRule.connectionId,
            ruleId = connRule.ruleId
        )
    ) {
        contentType(ContentType.Application.Json)
    }
        .body<List<ConnectionRule>>()
        .firstOrNull()
}

suspend fun ReportManager.report(connRules: List<ConnectionRule>): List<ConnectionRule> {
    return connRules.map { connRule ->
        exists(connRule)?.let { return@map it }
        client.post(ConnectionRules()) {
            contentType(ContentType.Application.Json)
            setBody(connRule)
        }.body()
    }
}
