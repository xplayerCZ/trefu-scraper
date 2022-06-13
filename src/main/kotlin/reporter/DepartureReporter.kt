package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackDeparture
import model.NewDeparture

@Serializable
@Resource("departures")
class Departures

suspend fun ReportManager.report(departures: List<NewDeparture>): List<CallbackDeparture> {
    return departures.map {
        client.post(Departures()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}

suspend fun ReportManager.report(departure: NewDeparture): CallbackDeparture {
    return client.post(Departures()) {
        contentType(ContentType.Application.Json)
        setBody(departure)
    }.body()

}
