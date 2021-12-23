import org.joda.time.DateTime
import org.joda.time.LocalDate

fun main() {
    val location = 11

    val scraper = Scraper(location)

    val packetData = scraper.requestPacketData()
    val today = LocalDate.now()
    val validPacket = packetData.find {
        it.valid && it.from < today && it.to > today
    }

    val lines = scraper.requestLines(validPacket!!.id, today)
    val stops = scraper.requestStops(validPacket.id)

    packetData.forEach {
        println(it)
    }

    lines.forEach {
        println(it)
    }

    stops.forEach {
        println(it)
    }

    val lineCode = lines.find { it.shortCode == "208"}!!.fullCode
    val timetables = scraper.requestTimetables(lineCode, 0, 11, validPacket.id, LocalDate.now())
    val route = scraper.requestRoute(lineCode, 0, 11, validPacket.id)
    val routeIds = route.map { stops.find { stop -> it.name == stop.name }?.id!! }

    timetables.forEach {
        println(it)
    }
    route.forEach {
        println(it)
    }
    routeIds.forEach {
        println(it)
    }
}