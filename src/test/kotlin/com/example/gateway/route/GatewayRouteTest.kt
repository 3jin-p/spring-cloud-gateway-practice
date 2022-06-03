package com.example.gateway.route

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class GatewayRouteTest(
    @Autowired
    val environment: Environment
) {

    lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + environment.getProperty("server.port")).build()
    }

    @Test
    fun testRouteFilter() {
        webTestClient.get().uri("/first-service/message").exchange()
            .expectHeader().valueEquals("first-response", "first-response-header2")

        webTestClient.get().uri("/second-service/message").exchange()
            .expectHeader().valueEquals("second-response", "second-response-header2")
    }

}