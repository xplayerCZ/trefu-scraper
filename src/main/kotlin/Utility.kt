import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.jsoup.Jsoup

object Utility {
    fun createPacketDataRequest(location: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListPacketJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun createStopsRequest(location: Int, packetId: Int): Request {
        val host = "https://www.mhdspoje.cz/jrw50/php/5_1/ListStaniceJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packetId.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun createLinesRequest(location: Int, packedId: Int, date: LocalDate, ptl: Int): Request  {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListLinkyJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packedId.toString())
            .addQueryParameter("datum", date.toString("d_M_yyyy"))
            .addQueryParameter("ptl", ptl.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun createRouteRequest(line: String, direction: Int, location: Int, packetId: Int): Request  {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListTrasyJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("linka", line)
            .addQueryParameter("smer", direction.toString())
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packetId.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun createTimetableRequest(lineFullCode: String, direction: Int, location: Int, packedId: Int, date: LocalDate, daily: Boolean): Request  {
        val host = "https://www.mhdspoje.cz/jrw50/php/5_1/loadJRJSON.php"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("linka", lineFullCode)
            .addQueryParameter("smer", direction.toString())
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packedId.toString())
            .addQueryParameter("datum", date.toString("d_M_yyyy"))
            .addQueryParameter("denni", if (daily) "1" else "0")

            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun extractArrayData(rawData: String): List<List<String>> {
        val startIndex = rawData.indexOf("[")
        val endIndex = rawData.lastIndexOf("]")

        val rawJSON = rawData.subSequence(startIndex, endIndex + 1).toString()
        return Json.decodeFromString(rawJSON)
    }

    //Fix last 0 in received data
    fun fixRouteData(rawData: String): String {
        return rawData.replace(",0", ",\"0\"")
    }

    fun extractTimetableData(rawData: String): List<Timetable> {
        val doc = Jsoup.parse(rawData)

        val timetables = mutableListOf<Timetable>()
        val tables = doc.select(".table_JR")
        for (table in tables) {
            val weekDay: Boolean
            val label = table.select("label[name=\"name_sloupec\"]")
            weekDay = label.text() == "Pracovní dny"

            val numbers = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td[title]").eachText()
            val notes = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td:not([title])").eachText()

            val rows = table.select("table.table_time_JR tr[id~=jr]")
            val columns = mutableListOf<List<String>>()
            for(row in rows) {
                columns.add(row.select("td").eachText())
            }

            val connectionsCount = columns[0].size
            val stopsCount = rows.size

            val timeMatrix = Array(connectionsCount) { Array(stopsCount) { "" } }

            for(i in 0 until stopsCount) {
                val column = columns[i]
                for(j in 0 until connectionsCount) {
                    timeMatrix[j][i] = column[j]
                }
            }

            val connections = mutableListOf<Connection>()
            for (i in 0 until numbers.size) {
                val times = timeMatrix[i].map {
                    if(!it.contains("-")) {
                        val tokens = it.split(':')
                        LocalTime(tokens[0].toInt(), tokens[1].toInt())
                    } else {
                        null
                    }
                }

                connections.add(Connection(numbers[i].toInt(), times, notes[i], weekDay))
            }
            timetables.add(Timetable(connections, weekDay))
        }
        return timetables
    }
}