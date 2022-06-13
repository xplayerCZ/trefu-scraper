package collector

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.RawLine
import scraper.CommonScraper
import serializer.source.LocalDateSerializer
import java.time.LocalDate


@Serializable
@Resource("ListLinkyJSON.php")
class SourceLines(
    val location: Int,
    val packet: Int,
    val datum: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val ptl: Int = 1
)

suspend fun CollectionManager.collect(location: Int, packetId: Int, date: LocalDate, ptl: Int = 1): List<RawLine> {

    val response = client.get(SourceLines(location, packetId, date, ptl))

    val dataMatrix = CommonScraper.scrape(response.body())
    val lines = dataMatrix.map {
        RawLine(it[0].toInt(), it[1].trim())
    }

    return lines
}
