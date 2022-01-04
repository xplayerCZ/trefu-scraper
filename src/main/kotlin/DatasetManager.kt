import collector.*
import model.*
import okhttp3.OkHttpClient
import reporter.*
import scraper.TimetableScraper
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class DatasetManager(private val location: Int = 11) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val dbUrl = "http://localhost:8080/"

    private val lineCollector = LineCollector(httpClient)
    private val packetCollector = PacketCollector(httpClient)
    private val routeCollector = RouteCollector(httpClient)
    private val stopCollector = StopCollector(httpClient)
    private val timetableCollector = TimetableCollector(httpClient)

    private val connectionReporter = ConnectionReporter(httpClient, dbUrl)
    private val lineReporter = LineReporter(httpClient, dbUrl)
    private val packetReporter = PacketReporter(httpClient, dbUrl)
    private val stopReporter = StopReporter(httpClient, dbUrl)
    private val routeReporter = RouteReporter(httpClient, dbUrl)

    fun update(from: LocalDate, to: LocalDate): Boolean {
        val packets = packetCollector.collect(location).map { NewPacket(it.id, it.from, it.to, it.valid) }
        val relevantPackets = packets.filter { it.valid && ((it.from <= from && to <= it.to) || (it.from < from && from < it.to) || (it.from < to && to < it.to)) }
        packetReporter.reportAll(packets)

        relevantPackets.forEach {
            updateData(it)
        }

        return true
    }

    private fun updateData(packet: NewPacket) {
        val stops = stopCollector.collect(location, packet.id).map { NewStop(it.id, it.name, it.latitude, it.longitude, it.code) }
        stopReporter.reportAll(stops)

        val lines = lineCollector.collect(location, packet.id, packet.from).map { NewLine(it.shortCode, it.fullCode, packet.id) }
        lineReporter.reportAll(lines)

        val timetables = lines.map {
            collectFromTimetables(it, packet, stops)
        }.flatten()

        timetables.forEach {
            connectionReporter.reportAll(it.connections)
        }
    }

    private fun collectFromTimetables(line: NewLine, packet: NewPacket, stops: List<NewStop>): List<Timetable> {
        val rawRoutes = listOf(
            routeCollector.collect(line.fullCode, 0, location, packet.id ),
            routeCollector.collect(line.fullCode, 1, location, packet.id )
        )

        val rawTimetables = listOf(
            timetableCollector.collect(line.fullCode, 0, location, packet.id, packet.from),
            timetableCollector.collect(line.fullCode, 1, location, packet.id, packet.to)
        )

        val results = mutableListOf<Timetable>()
        for(i in 0..1) {
            results.add(scrapeDataFromTimetables(rawRoutes[i], rawTimetables[i], stops, i, line))
        }
        return results
    }

    private fun scrapeDataFromTimetables(routeStops: List<RawRouteStop>, timetable: String, stops: List<NewStop>, direction: Int, line: NewLine): Timetable {
        val stopIdsInRoute = routeStops.map { routeStop -> stops.find { stop -> routeStop.name == stop.name }!!.id }
        val scrapedData = TimetableScraper.scrape(timetable)
        val enabledStopIds = scrapedData.enabledStopsIndexes.map { stopIdsInRoute[it] }

        val routeId = routeReporter.report(NewRoute(direction, stopIdsInRoute, enabledStopIds, line.fullCode)).id

        val newConnections = scrapedData.connections.map {
            NewConnection(routeId, it.number, it.departureTimes.map { time -> if(!time.contains('-')) LocalTime.parse(time) else null }, emptyList())
        }

        return Timetable(newConnections)
    }
}