import collector.*
import model.*
import okhttp3.OkHttpClient
import reporter.*
import java.time.LocalDate
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

    private val lineReporter = LineReporter(httpClient, dbUrl)
    private val packetReporter = PacketReporter(httpClient, dbUrl)
    private val stopReporter = StopReporter(httpClient, dbUrl)
    private val timetableReporter = TimetableReporter(httpClient, dbUrl)
    private val routeReporter = RouteReporter(httpClient, dbUrl)
    private val departureReporter = DepartureReporter(httpClient, dbUrl)

    fun update(from: LocalDate, to: LocalDate): Boolean {
        val packets = collectPackets()

        packetReporter.reportAll(packets.map { PacketDTO(it.id, it.from, it.to, it.valid) })
        val relevantPackets = packets.filter { it.valid && ((it.from <= from && to <= it.to) || (it.from < from && from < it.to) || (it.from < to && to < it.to)) }
        relevantPackets.forEach {
            updateData(it)
        }

        return true
    }

    private fun filterValidPacket(packets: List<Packet>, date: LocalDate) =
        packets.find {
            it.valid && it.from < date && it.to > date
        } ?: throw Exception("No packets found for specified date. Validate packet collection procedure!")

    private fun collectPackets() = packetCollector.collect(location)

    private fun updateData(packet: Packet) {
        val date = packet.from

        val lines = lineCollector.collect(location, packet.id, date)
        val stops = stopCollector.collect(location, packet.id)
        val timetables = collectAllTimetables(packet.id, lines, date)
        val routes = collectAllRoutes(packet.id, lines, stops)
        val departures = collectAllDepartures(timetables, packet.id)

        lineReporter.reportAll(lines.map { LineDTO(it.fullCode, it.shortCode) })
        stopReporter.reportAll(stops.map { StopDTO(it.id, it.name, it.latitude, it.longitude, it.code.toInt()) })
        reportAllTimetables(timetables, packet.id)
        routeReporter.reportAll(routes)
        departureReporter.reportAll(departures)
    }

    private fun collectAllTimetables(packetId: Int, lines: List<Line>, date: LocalDate): List<Timetable> {
        val timetableCollection = mutableListOf<Timetable>()
        lines.forEach {
            timetableCollection.addAll(
                timetableCollector.collect(it.fullCode, 0, location, packetId, date)
                    .map { timetable ->
                        timetable.apply {
                            lineFullCode = it.fullCode
                            direction = 0
                        }
                    })

            timetableCollection.addAll(
                timetableCollector.collect(it.fullCode, 1, location, packetId, date)
                    .map { timetable ->
                        timetable.apply {
                            lineFullCode = it.fullCode
                            direction = 1
                        }
                    })
        }
        return timetableCollection
    }

    private fun collectAllRoutes(packetId: Int, lines: List<Line>, stops: List<Stop>): List<RouteDTO> {
        val routeCollection = mutableListOf<RouteDTO>()
        lines.forEach {
            routeCollection.add(RouteDTO(routeCollector.collect(it.fullCode, 0, location, packetId).map { route -> stops.find { stop -> stop.name == route.name }!!.id }, 0, it.fullCode, packetId ))
            routeCollection.add(RouteDTO(routeCollector.collect(it.fullCode, 1, location, packetId).map { route -> stops.find { stop -> stop.name == route.name }!!.id }, 1, it.fullCode, packetId))
        }
        return routeCollection
    }

    private fun collectAllDepartures(timetables: List<Timetable>, packetId: Int): List<DepartureDTO> {
        val departures = mutableListOf<DepartureDTO>()
        timetables.forEach { timetable ->
            timetable.connections.forEach {
                departures.addAll(it.departures.mapIndexed { index, departure -> DepartureDTO(departure, it.number, timetable.direction!!, timetable.lineFullCode!!, packetId, index)})
            }
        }
        return departures
    }

    private fun reportAllTimetables(timetables: List<Timetable>, packetId: Int) {

        val timetablesToReport = timetables.map { timetable ->
            val connectionDTOs = timetable.connections.map { ConnectionDTO(it.number, it.notes) }
            TimetableDTO(packetId, timetable.lineFullCode!!, timetable.weekDays, connectionDTOs, timetable.valid, timetable.direction!!)
        }
        timetableReporter.reportAll(timetablesToReport)
    }
}