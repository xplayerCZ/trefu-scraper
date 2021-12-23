import Utility.extractTimetableData
import Utility.fixRouteData
import okhttp3.OkHttpClient
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit


data class Packet(val id: Int, val from: LocalDate,val to: LocalDate, val valid: Boolean)
data class Line(val fullCode: String, val shortCode: String)
data class Stop(val id: Int, val name: String, val latitude: String, val longitude: String, val code: String)
data class LineStop(val lineId: Int, val stopId: Int)
data class Connection(val number: Int, val departures: List<LocalTime?>, val notes: String, val weekDays: Boolean)
data class Timetable(val connections: List<Connection>, val weekDays: Boolean)
data class Route(val id: Int, val name: String)

class Scraper(private val location: Int = 11) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun requestPacketData(): List<Packet> {

        val request = Utility.createPacketDataRequest(location)
        val response = okHttpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = Utility.extractArrayData(raw)
        val packets = dataMatrix.map {
            val from = LocalDate(it[3].toInt(), it[2].toInt(), it[1].toInt())
            val to = LocalDate(it[6].toInt(), it[5].toInt(), it[4].toInt())
            Packet(it[0].toInt(), from, to,it[7] == "1")
        }

        return packets
    }

    fun requestStops(packetId: Int): List<Stop>  {

        val request = Utility.createStopsRequest(location, packetId)
        val response = okHttpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = Utility.extractArrayData(raw)
        val stops = dataMatrix.map {
           Stop(it[3].toInt(), it[0], it[1], it[2], it[4])
        }

        return stops
    }

    fun requestLines(packedId: Int, date: LocalDate, ptl: Int = 1): List<Line> {

        val request = Utility.createLinesRequest(location, packedId, date, ptl)
        val response = okHttpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        val dataMatrix = Utility.extractArrayData(raw)
        val lines = dataMatrix.map {
            Line(it[0], it[1].trim())
        }

        return lines
    }

    fun requestRoute(line: String, direction: Int, location: Int, packetId: Int): List<Route> {

        val request = Utility.createRouteRequest(line, direction, location, packetId)
        val response = okHttpClient.newCall(request).execute()
        val raw = response.body?.string()!!


        val dataMatrix = Utility.extractArrayData(fixRouteData(raw))
        val routes = dataMatrix.map {
            Route(it[0].toInt(), it[1])
        }

        return routes
    }

    fun requestTimetables(
        lineFullCode: String,
        direction: Int,
        location: Int,
        packedId: Int,
        date: LocalDate,
        daily: Boolean = false,
    ): List<Timetable> {

        val request = Utility.createTimetableRequest(lineFullCode, direction, location, packedId, date, daily)
        val response = okHttpClient.newCall(request).execute()
        val raw = response.body?.string()!!

        return extractTimetableData(raw)
    }
}