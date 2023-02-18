import collector.CollectionManager
import collector.collect
import model.*
import reporter.ReportManager
import reporter.report
import scraper.TimetableScraper

class DatasetManager(
    private val location: Int,
    private val errors: MutableList<String> = mutableListOf(),
) {

    suspend fun update(start: Int) {
        val packets = CollectionManager.collect(location)
            .filter { it.valid && it.code > start }
            .map { NewPacket(it.from, it.to, it.valid, it.code) }
            .let { ReportManager.report(it) }

        packets.forEach {
            updateData(it)
        }

        if (errors.isNotEmpty()) {
            println("Errors:")
            errors.forEach { println(it) }
        }
    }

    private suspend fun updateData(packet: CallbackPacket) {
        val stops = CollectionManager
            .collect(location, packet.code)
            .map { NewStop(it.name, it.latitude, it.longitude, it.code) }
            .let { ReportManager.report(it) }

        val lines = CollectionManager
            .collect(location, packet.code, packet.from)
            .map { NewLine(it.shortCode, it.fullCode, packet.id) }
            .let { ReportManager.report(it) }

        lines.forEach { collectFromTimetables(it, packet, stops) }

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
            scrapeDataFromTimetables(rawRoutes[i], rawTimetables[i], stops, i, line, packet)
        }
    }

    private suspend fun scrapeDataFromTimetables(
        routeStops: List<RawRouteStop>,
        timetable: String,
        stops: List<CallbackStop>,
        direction: Int,
        line: CallbackLine,
        packet: CallbackPacket,
    ) {
        val stopIdsInRoute = routeStops.map { routeStop -> stops.find { stop -> routeStop.name == stop.name }!!.id }
        val scrapedData = TimetableScraper.scrape(timetable)
        val enabledStopIds = scrapedData.enabledStopsIndexes.map { stopIdsInRoute[it] }

        val routeId = ReportManager.report(NewRoute(line.id, stopIdsInRoute.size)).id

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

            it.notes
                .split(',')
                .asSequence()
                .map { note -> note.trim() }
                .filter { note -> note.isNotEmpty() }
                .mapNotNull { note -> getRuleIdByNote(note, direction, line.fullCode, packet) }
                .map { ruleId -> ConnectionRule(connection.id, ruleId) }
                .distinct()
                .toList()
                .let { connRules -> ReportManager.report(connRules) }
        }
    }

    private val unhandledRules = listOf("D", "P", "y", "O", "G", "J")

    private fun getRuleIdByNote(note: String, dir: Int, line: Int, packet: CallbackPacket): Int? {
        val isSummer = packet.from.month.value in 7..8 && packet.to.month.value in 7..8
        return when (note) {
            "X" -> if (!isSummer) 1 else 4
            "25", "+" -> 2
            "6" -> 3
            in unhandledRules -> null
            else -> {
                errors.add("$note was found for dir $dir for line $line, skipping...")
                null
            }
        }
    }
}