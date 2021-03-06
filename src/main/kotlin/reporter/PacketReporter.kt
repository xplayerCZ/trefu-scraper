package reporter

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.CallbackPacket
import model.NewPacket

@Serializable
@Resource("packets")
class Packets

suspend fun ReportManager.report(packets: List<NewPacket>): List<CallbackPacket> {
    return packets.map {
        client.post(Packets()) {
            contentType(ContentType.Application.Json)
            setBody(it)
        }.body()
    }
}