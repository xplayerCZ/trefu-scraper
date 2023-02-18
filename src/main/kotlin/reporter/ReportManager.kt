package reporter

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import util.okHttpDefault

object ReportManager {
    val client = HttpClient(OkHttp) {
        okHttpDefault()
        defaultRequest {
            host = System.getenv("REPORTER_HOST") ?: "localhost"
            port = 8080
            url { protocol = URLProtocol.HTTP }
        }
    }
}