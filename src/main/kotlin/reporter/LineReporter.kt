package reporter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.CallbackLine
import model.CallbackStop
import model.NewLine
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class LineReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(lines: List<NewLine>): List<CallbackLine> =
        lines.map {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            if(response.code / 100 > 5) throw Exception("Invalid request!")

            Json.decodeFromString(response.body!!.string())
        }

    private fun createRequest(line: NewLine): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("line")
            .build()

        val requestBody =
            Json.encodeToString(line).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    fun getStored(): List<CallbackLine> {
        val request = createStoredRequest()
        val response = httpClient.newCall(request).execute()

        return Json.decodeFromString(response.body!!.string())
    }

    private fun createStoredRequest(): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("line")
            .build()

        return Request.Builder()
            .url(url)
            .get()
            .build()
    }
}