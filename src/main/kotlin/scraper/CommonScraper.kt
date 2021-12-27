package scraper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object CommonScraper {

    fun scrape(raw: String): List<List<String>>{
        val startIndex = raw.indexOf("[")
        val endIndex = raw.lastIndexOf("]")

        if(startIndex == -1 || endIndex == -1) return listOf()

        val rawJSON = raw.subSequence(startIndex, endIndex + 1).toString()
        return Json.decodeFromString(rawJSON)
    }
}