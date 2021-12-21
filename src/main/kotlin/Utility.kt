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

    fun extractTimetableData(rawData: String): List<Timetable> {
        val doc = Jsoup.parse(rawData)

        val timetables = mutableListOf<Timetable>()
        val tables = doc.select(".table_JR")
        for (table in tables) {
            val weekDay: Boolean
            val label = table.select("label[name=\"name_sloupec\"]")
            weekDay = label.text() == "Pracovní dny"

            val clickableImages = table.select(".zastavky img[onclick]").eachAttr("onclick")
            val stopIds = clickableImages.map { parseCallback(it) }

            val numbers = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td[title]").eachText()
            val notes = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td:not([title])").eachText()

            val timeMatrix = mutableListOf<MutableList<String>>()

            val rows = table.select("table.table_time_JR tr[id|=jr]")
            for(row in rows) {
                timeMatrix.add(row.select("td").eachText())
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
            timetables.add(Timetable(stopIds, connections, weekDay))
        }
        return timetables
    }

    fun parseCallback(onclick: String): Int {
        val tokens = onclick.split(",)")

        return tokens[tokens.size - 2].toInt()
    }
}