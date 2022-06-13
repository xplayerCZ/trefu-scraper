package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackLine
import model.NewLine

@Serializable
@Resource("lines")
class Lines

suspend fun ReportManager.report(lines: List<NewLine>): List<CallbackLine> {
    return lines.map {
        client.post(Lines()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}
