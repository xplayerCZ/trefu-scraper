package util

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

fun HttpClientConfig<OkHttpConfig>.okHttpDefault() {
    default()
    engine {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = Level.BODY
        addInterceptor(loggingInterceptor)
    }
}

fun HttpClientConfig<*>.default() {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 5)
        exponentialDelay()
    }
    install(DefaultRequest)
    install(Resources)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}