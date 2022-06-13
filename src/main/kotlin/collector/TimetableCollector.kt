package collector

import io.ktor.client.plugins.resources.*
import io.ktor.client.statement.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import serializer.source.LocalDateSerializer
import java.time.LocalDate

@Serializable
@Resource("loadJRJSON.php")
class SourceTimetables(
    val linka: Int,
    val smer: Int,
    val location: Int,
    val packet: Int,
    val datum: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val denni: Int
)

suspend fun CollectionManager.collect(
    lineFullCode: Int,
    direction: Int,
    location: Int,
    packetId: Int,
    date: LocalDate,
    daily: Boolean = false,
): String {

    val response = client.get(
        SourceTimetables(
            linka = lineFullCode,
            smer = direction,
            location = location,
            packet = packetId,
            datum = date,
            denni = if (daily) 1 else 0
        )
    )

    return response.bodyAsText(Charsets.ISO_8859_1)
}