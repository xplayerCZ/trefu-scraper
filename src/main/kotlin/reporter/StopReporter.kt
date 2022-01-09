package reporter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.CallbackStop
import model.NewStop
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class StopReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {
    fun reportAll(stops: List<NewStop>) {

        stops.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            if(response.code / 100 > 5) throw Exception("Invalid request!")
            response.close()
        }
    }

    private fun createRequest(stop: NewStop): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("stop")
            .build()

        val requestBody =
            Json.encodeToString(stop).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    fun getStored(): List<CallbackStop> {
        val request = createStoredRequest()
        val response = httpClient.newCall(request).execute()

        return Json.decodeFromString(response.body!!.string())
    }

    private fun createStoredRequest(): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("stop")
            .build()

        return Request.Builder()
            .url(url)
            .get()
            .build()
    }
}