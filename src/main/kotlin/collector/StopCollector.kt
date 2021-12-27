package collector

import model.Stop
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import scraper.CommonScraper

class StopCollector(private val httpClient: OkHttpClient) {

    fun collect(location: Int, packetId: Int): List<Stop>  {

        val request = createRequest(location, packetId)
        val response = httpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = CommonScraper.scrape(raw)
        val stops = dataMatrix.map {
            Stop(it[3].toInt(), it[0], it[1], it[2], it[4])
        }

        return stops
    }

    private fun createRequest(location: Int, packetId: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/5_1/ListStaniceJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packetId.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

}