package collector

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.RawStop
import scraper.CommonScraper

@Serializable
@Resource("ListStaniceJSON.php")
class SourceStops(
    val location: Int,
    val packet: Int
)

suspend fun CollectionManager.collect(location: Int, packetId: Int): List<RawStop> {

    val response = client.get(SourceStops(location, packetId))

    val dataMatrix = CommonScraper.scrape(response.body())
    val stops = dataMatrix.map {
        RawStop(it[0], it[1], it[2], it[4].toInt())
    }

    return stops
}
