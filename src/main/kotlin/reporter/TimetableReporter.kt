package reporter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Timetable
import model.TimetableDTO
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TimetableReporter (
    private val httpClient: OkHttpClient,
    private val host: String
) {
    
    fun reportAll(timetables: List<TimetableDTO>) {

        timetables.forEach {
            val request = createRequest(it)
            val response = httpClient.newCall(request).execute()

            println(response.message)
            response.close()
        }
    }

    private fun createRequest(timetable: TimetableDTO): Request {
        val url = host.toHttpUrl().newBuilder()
            .addPathSegment("timetable")
            .build()

        val requestBody =
            Json.encodeToString(timetable).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }
}