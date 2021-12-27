package reporter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Connection
import model.ConnectionDTO
import model.Route
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.LocalDate
import scraper.CommonScraper

class ConnectionReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(connections: List<ConnectionDTO>) {

        connections.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            response.close()
        }
    }

    private fun createRequest(connection: ConnectionDTO): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("connection")
            .build()

        val requestBody =
            Json.encodeToString(connection).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .put(requestBody)
            .build()
    }
}