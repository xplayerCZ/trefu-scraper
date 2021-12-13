import org.joda.time.LocalDate

fun main() {
    val location = 11

    val scraper = Scraper(location)

    val packetData = scraper.requestPacketData()
    val today = LocalDate.now()
    val validPacket = packetData.find {
        it.valid && it.from < today && it.to > today
    }

    val lines = scraper.requestLines(validPacket!!.id!!, today)
    val stops = scraper.requestStops(validPacket.id!!)

    packetData.forEach {
        println(it)
    }

    lines.forEach {
        println(it)
    }

    stops.forEach {
        println(it)
    }
}