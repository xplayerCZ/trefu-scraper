import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

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

    fun createLinesRequest(location: Int, packedId: Int, day: Int, month: Int, year: Int, ptl: Int): Request  {
        val host = "https://www.mhdspoje.cz/jrw50/php/ListLinkyJSON.php"

        val date = "${day}_${month}_${year}"

        val url = host.toHttpUrl().newBuilder()
            .addQueryParameter("location", location.toString())
            .addQueryParameter("packet", packedId.toString())
            .addQueryParameter("datum", date)
            .addQueryParameter("ptl", ptl.toString())
            .build()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun extractArrayData(rawData: String): List<List<String>> {
        val startIndex = rawData.indexOf("[")
        val endIndex = rawData.lastIndexOf("]")

        val rawJSON = rawData.subSequence(startIndex, endIndex + 1).toString();
        return Json.decodeFromString<List<List<String>>>(rawJSON)
    }
}