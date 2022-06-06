package com.example.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GlobalFilter(): AbstractGatewayFilterFactory<GlobalFilter.Config>(Config::class.java) {

    data class Config(
        val baseMessage: String,
        val preLogger: Boolean,
        val postLogger: Boolean
    ) {
    }

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request: ServerHttpRequest = exchange.request
            val response: ServerHttpResponse = exchange.response

            println("Glboal Filter baseMessage: ${config!!.baseMessage}")

            if (config.preLogger) {
                println("Global Filter Start: request id -> ${request.id}")
            }

            return@GatewayFilter chain.filter(exchange).then(Mono.fromRunnable {
                if (config.postLogger) {
                    println("Global Filter End: response code -> ${response.statusCode}")
                }
            })
        }
    }
}