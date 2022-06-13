package collector

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import util.okHttpDefault

object CollectionManager {
    val client = HttpClient(OkHttp) {
        okHttpDefault()
        defaultRequest {
            url("https://www.mhdspoje.cz/jrw50/php/5_1/")
        }
    }
}