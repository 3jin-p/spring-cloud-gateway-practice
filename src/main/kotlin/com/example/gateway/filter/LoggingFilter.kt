package com.example.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoggingFilter(): AbstractGatewayFilterFactory<LoggingFilter.Config>(Config::class.java) {

    data class Config(
        val baseMessage: String,
        val preLogger: Boolean,
        val postLogger: Boolean
    ) {
    }

    override fun apply(config: Config?): GatewayFilter {
        val filter: GatewayFilter = OrderedGatewayFilter(GatewayFilter { exchange, chain ->
            val request: ServerHttpRequest = exchange.request
            val response: ServerHttpResponse = exchange.response

            println("Logging Filter baseMessage: ${config!!.baseMessage}")

            if (config.preLogger) {
                println("Logging Filter Start: request id -> ${request.id}")
            }

            return@GatewayFilter chain.filter(exchange).then(Mono.fromRunnable {
                if (config.postLogger) {
                    println("Logging Filter End: response code -> ${response.statusCode}")
                }
            })
        }, Ordered.LOWEST_PRECEDENCE)

        return filter
    }
}