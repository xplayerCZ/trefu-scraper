package collector

import model.Route
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import scraper.CommonScraper

class RouteCollector(private val httpClient: OkHttpClient) {

    fun collect(line: Int, direction: Int, location: Int, packetId: Int): List<Route> {

        val request = createRequest(line, direction, location, packetId)
        val response = httpClient.newCall(request).execute()
        val raw = response.body?.string()!!


        val dataMatrix = CommonScraper.scrape(fixRouteData(raw))
        val routes = dataMatrix.map {
            Route(it[0].toInt(), it[1])
        }

        return routes
    }

    private fun createRequest(line: Int, direction: Int, location: Int, packetId: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListTrasyJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("linka", line.toString())
            .addQueryParameter("smer", direction.toString())
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packetId.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    //Fix last 0 in received data
    private fun fixRouteData(rawData: String): String {
        return rawData.replace(",0", ",\"0\"")
    }
}