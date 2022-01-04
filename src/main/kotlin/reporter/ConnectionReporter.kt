package reporter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.NewConnection
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ConnectionReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(connections: List<NewConnection>) {

        connections.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            if(response.code / 100 > 5) throw Exception("Invalid request!")
            response.close()
        }
    }

    private fun createRequest(connection: NewConnection): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("connection")
            .build()

        val requestBody =
            Json.encodeToString(connection).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }
}