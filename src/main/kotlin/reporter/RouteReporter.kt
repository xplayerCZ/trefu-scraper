package reporter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.NewRoute
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import scraper.CommonScraper

class RouteReporter(
    private val httpClient: OkHttpClient,
    private val host: String
) {

    fun reportAll(routes: List<NewRoute>) {

        routes.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            response.close()
        }
    }

    private fun createRequest(route: NewRoute): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("route")
            .build()

        val requestBody =
            Json.encodeToString(route).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }
}
