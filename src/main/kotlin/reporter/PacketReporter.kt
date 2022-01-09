package reporter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.CallbackPacket
import model.CallbackStop
import model.NewPacket
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PacketReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(packets: List<NewPacket>) {
        packets.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            if(response.code / 100 > 5) throw Exception("Invalid request!")
            response.close()
        }
    }

    private fun createRequest(packet: NewPacket): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("packet")
            .build()

        val requestBody =
            Json.encodeToString(packet).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    fun getStored(): List<CallbackPacket> {
        val request = createStoredRequest()
        val response = httpClient.newCall(request).execute()

        return Json.decodeFromString(response.body!!.string())
    }

    private fun createStoredRequest(): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("packet")
            .build()

        return Request.Builder()
            .url(url)
            .get()
            .build()
    }
}
