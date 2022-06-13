package collector

import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import model.RawPacket
import scraper.CommonScraper
import java.time.LocalDate

@Serializable
@Resource("ListPacketJSON.php")
class SourcePackets(val location: Int)

suspend fun CollectionManager.collect(location: Int): List<RawPacket> {

    val response = client.get(SourcePackets(location))

    val dataMatrix = CommonScraper.scrape(response.body())
    val packets = dataMatrix.map {
        val from = LocalDate.of(it[3].toInt(), it[2].toInt(), it[1].toInt())
        val to = LocalDate.of(it[6].toInt(), it[5].toInt(), it[4].toInt())
        RawPacket(from, to, it[7] == "1", it[0].toInt())
    }

    return packets
}