import collector.CollectionManager
import collector.collect
import model.*
import reporter.ReportManager
import reporter.report
import scraper.TimetableScraper
import java.time.LocalDate

class DatasetManager(private val location: Int = 11) {

    suspend fun update(from: LocalDate, to: LocalDate) {
        val packets = CollectionManager.collect(location).map { NewPacket(it.from, it.to, it.valid, it.code) }
        //val relevantPackets = packets.filter { it.valid && ((it.from <= from && to <= it.to) || (it.from < from && from < it.to) || (it.from < to && to < it.to)) }

        val createdPackets = ReportManager.report(packets)
        val relevantPackets = createdPackets.filter { it.valid && (it.from >= from || it.to >= from) }

        relevantPackets.forEach {
            updateData(it)
        }
    }

    private suspend fun updateData(packet: CallbackPacket) {
        val stops = CollectionManager.collect(location, packet.code)
            .map { NewStop(it.name, it.latitude, it.longitude, it.code) }
        val createdStops = ReportManager.report(stops)

        val lines = CollectionManager.collect(location, packet.code, packet.from)
            .map { NewLine(it.shortCode, it.fullCode, packet.id) }
        val createdLines = ReportManager.report(lines)

        createdLines.forEach { collectFromTimetables(it, packet, createdStops) }

    }

    private suspend fun collectFromTimetables(line: CallbackLine, packet: CallbackPacket, stops: List<CallbackStop>) {
        val rawRoutes = listOf(
            CollectionManager.collect(line.fullCode, 0, location, packet.code),
            CollectionManager.collect(line.fullCode, 1, location, packet.code)
        )

        val rawTimetables = listOf(
            CollectionManager.collect(line.fullCode, 0, location, packet.code, packet.from),
            CollectionManager.collect(line.fullCode, 1, location, packet.code, packet.to)
        )

        for (i in 0..1) {
            scrapeDataFromTimetables(rawRoutes[i], rawTimetables[i], stops, i, line)
        }
    }

    private suspend fun scrapeDataFromTimetables(
        routeStops: List<RawRouteStop>,
        timetable: String,
        stops: List<CallbackStop>,
        direction: Int,
        line: CallbackLine
    ) {
        val stopIdsInRoute = routeStops.map { routeStop -> stops.find { stop -> routeStop.name == stop.name }!!.id }
        val scrapedData = TimetableScraper.scrape(timetable)
        val enabledStopIds = scrapedData.enabledStopsIndexes.map { stopIdsInRoute[it] }

        val routeId = ReportManager.report(NewRoute(line.id, stopIdsInRoute.size, direction)).id

        stopIdsInRoute.forEachIndexed { index, stopId ->
            ReportManager.report(RouteStop(stopId, routeId, index, enabledStopIds.contains(stopId)))
        }

        scrapedData.connections.forEach {
            val connection = ReportManager.report(
                NewConnection(
                    routeId,
                    it.number
                )
            )
            it.departureTimes.forEachIndexed { index, departureTime ->
                ReportManager.report(NewDeparture(connection.id, departureTime, index))
            }
        }

        /*
                        it.departureTimes.map { time -> if(!time.contains('-')) LocalTime.parse(time) else null },
                it.notes.split(',').map { note -> note.trim() }.filter { note -> note.isNotEmpty() }
                    .mapNotNull { note -> getRuleIdByNote(note, direction, line.fullCode) }.distinct()
         */

    }

    private fun getRuleIdByNote(note: String, dir: Int, line: Int): Int? =
        when(note) {
            "X"-> 1
            "25", "+" -> 2
            "6" -> 3
            "D", "P", "y", "O" -> null
            else -> throw Exception("$note was found for dir $dir for line $line")
        }
}