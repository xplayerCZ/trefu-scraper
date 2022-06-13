package scraper

import model.RawConnection
import model.RawTimetable
import org.jsoup.Jsoup
import java.time.LocalTime

object TimetableScraper {
    fun scrape(raw: String): RawTimetable {
        val doc = Jsoup.parse(raw)
        val connections = mutableListOf<RawConnection>()
        val enabledStopsIndexes = mutableListOf<Int>()

        val tables = doc.select(".table_JR")
        for (table in tables) {

            val connectionNumbers = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td[title]").eachText().map { it.toInt() }
            val connectionNotes = table.select("table.table_time_JR tr.cell_time_jr_zahlavi td:not([title])").eachText()

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
                var departuresCount = 0
                for(j in 0 until connectionsCount) {
                    timeMatrix[j][i] = column[j]
                    if(!column[j].contains('-')) departuresCount++
                }
                if(departuresCount > 0) enabledStopsIndexes.add(i)
            }

            for (i in connectionNumbers.indices) {
                val departureTimes = timeMatrix[i]
                val existing = connections.find { it.number == connectionNumbers[i] }

                when (existing) {
                    null -> connections.add(
                        RawConnection(
                            connectionNumbers[i],
                            departureTimes.toList().map { if (!it.contains('-')) LocalTime.parse(it) else null },
                            connectionNotes[i]
                        )
                    )
                    else -> existing.notes += ", " + connectionNotes[i]
                }
            }
        }
        return RawTimetable(connections, enabledStopsIndexes.distinct())
    }
}