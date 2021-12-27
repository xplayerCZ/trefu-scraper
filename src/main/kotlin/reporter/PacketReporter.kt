package reporter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Packet
import model.PacketDTO
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PacketReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(packets: List<PacketDTO>) {

        packets.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            response.close()
        }
    }

    private fun createRequest(packet: PacketDTO): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("packet")
            .build()

        val requestBody =
            Json.encodeToString(packet).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .put(requestBody)
            .build()
    }
}