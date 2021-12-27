package collector

import model.Packet
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.joda.time.LocalDate
import scraper.CommonScraper

class PacketCollector(private val httpClient: OkHttpClient) {

    fun collect(location: Int): List<Packet> {

        val request = createRequest(location)
        val response = httpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = CommonScraper.scrape(raw)
        val packets = dataMatrix.map {
            val from = LocalDate(it[3].toInt(), it[2].toInt(), it[1].toInt())
            val to = LocalDate(it[6].toInt(), it[5].toInt(), it[4].toInt())
            Packet(it[0].toInt(), from, to,it[7] == "1")
        }

        return packets
    }

    private fun createRequest(location: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListPacketJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }
}