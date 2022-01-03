package collector

import model.RawLine
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import scraper.CommonScraper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LineCollector(private val httpClient: OkHttpClient) {

    fun collect(location: Int, packedId: Int, date: LocalDate, ptl: Int = 1): List<RawLine> {

        val request = createRequest(location, packedId, date, ptl)
        val response = httpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = CommonScraper.scrape(raw)
        val lines = dataMatrix.map {
            RawLine(it[0].toInt(), it[1].trim())
        }

        return lines
    }

    private fun createRequest(location: Int, packedId: Int, date: LocalDate, ptl: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListLinkyJSON.php"
        val formatter = DateTimeFormatter.ofPattern("d_M_yyyy")

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packedId.toString())
            .addQueryParameter("datum", formatter.format(date))
            .addQueryParameter("ptl", ptl.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }
}