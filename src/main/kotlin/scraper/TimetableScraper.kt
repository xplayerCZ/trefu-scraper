package scraper

import model.NewStop
import model.RawRouteStop
import model.Timetable
import org.jsoup.Jsoup
import java.time.LocalTime

// TODO: Rework scraper
object TimetableScraper {
    fun scrape(raw: String, routes: List<RawRouteStop>, stops: List<NewStop>): Timetable {
        val result = Timetable()

        val doc = Jsoup.parse(raw)
        val tables = doc.select(".table_JR")
        for (table in tables) {

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

            for (i in 0 until numbers.size) {
                val times = timeMatrix[i].map {
                    if(!it.contains("-")) {
                        val tokens = it.split(':')
                        LocalTime.of(tokens[0].toInt(), tokens[1].toInt())
                    } else {
                        null
                    }
                }
            }
        }
        return result
    }
}