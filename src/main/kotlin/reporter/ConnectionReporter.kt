package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackConnection
import model.NewConnection

@Serializable
@Resource("connections")
class Connections

suspend fun ReportManager.report(connections: List<NewConnection>): List<CallbackConnection> {
    return connections.map {
        client.post(Connections()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}

suspend fun ReportManager.report(connection: NewConnection): CallbackConnection {
    return client.post(Connections()) {
        contentType(ContentType.Application.Json)
        setBody(connection)
    }.body()
}
