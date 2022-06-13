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
class ConnectionRules

suspend fun ReportManager.report(connRules: List<ConnectionRule>): List<ConnectionRule> {
    return connRules.map {
        client.post(ConnectionRules()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}
