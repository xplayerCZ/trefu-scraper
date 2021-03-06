package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackRoute
import model.NewRoute

@Serializable
@Resource("routes")
class Routes

suspend fun ReportManager.report(routes: List<NewRoute>): List<CallbackRoute> {
    return routes.map {
        client.post(Routes()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}

suspend fun ReportManager.report(route: NewRoute): CallbackRoute {
    return client.post(Routes()) {
        contentType(ContentType.Application.Json)
        setBody(route)
    }.body()
}
