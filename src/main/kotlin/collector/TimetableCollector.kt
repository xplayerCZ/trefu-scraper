package collector

import model.Connection
import model.Timetable
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.jsoup.Jsoup
import scraper.TimetableScraper

class TimetableCollector(private val httpClient: OkHttpClient) {
    fun collect(
        lineFullCode: Int,
        direction: Int,
        location: Int,
        packedId: Int,
        date: LocalDate,
        daily: Boolean = false,
    ): List<Timetable> {

        val request = createRequest(lineFullCode, direction, location, packedId, date, daily)
        val response = httpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        return TimetableScraper.scrape(raw)
    }

    fun createRequest(lineFullCode: Int, direction: Int, location: Int, packedId: Int, date: LocalDate, daily: Boolean): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/5_1/loadJRJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("linka", lineFullCode.toString())
            .addQueryParameter("smer", direction.toString())
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packedId.toString())
            .addQueryParameter("datum", date.toString("d_M_yyyy"))
            .addQueryParameter("denni", if (daily) "1" else "0")

            .build()

        return Request.Builder()
            .url(url)
            .build()
    }
}