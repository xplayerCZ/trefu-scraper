package reporter


import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackStop
import model.NewStop

@Serializable
@Resource("stops")
class Stops

suspend fun ReportManager.report(stops: List<NewStop>): List<CallbackStop> {
    return stops.map {
        client.post(Stops()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}
