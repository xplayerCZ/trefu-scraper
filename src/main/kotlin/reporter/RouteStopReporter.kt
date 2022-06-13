package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.RouteStop

@Serializable
@Resource("route-stops")
class RouteStops

suspend fun ReportManager.report(routeStops: List<RouteStop>): List<RouteStop> {
    return routeStops.map {
        client.post(RouteStops()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}

suspend fun ReportManager.report(routeStop: RouteStop): RouteStop {
    return client.post(RouteStops()) {
        contentType(ContentType.Application.Json)
        setBody(routeStop)
    }.body()

}
