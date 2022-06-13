package collector

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.RawRouteStop
import scraper.CommonScraper

@Serializable
@Resource("ListTrasyJSON.php")
class SourceRoutes(
    val linka: Int,
    val smer: Int,
    val location: Int,
    val packet: Int
)

suspend fun CollectionManager.collect(line: Int, direction: Int, location: Int, packetId: Int): List<RawRouteStop> {

    val response = client.get(SourceRoutes(line, direction, location, packetId))

    val dataMatrix = CommonScraper.scrape(fixRouteData(response.body()))
    val routes = dataMatrix.map {
        RawRouteStop(it[1])
    }

    return routes
}

//Fix last 0 in received data
private fun fixRouteData(rawData: String): String {
    return rawData.replace(",0", ",\"0\"")
}