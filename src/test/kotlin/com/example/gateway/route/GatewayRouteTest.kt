package com.example.gateway.route

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
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

    @BeforeEach
    fun beforeEach() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + environment.getProperty("server.port")).build()
    }

    lateinit var webTestClient: WebTestClient

    @Test
    fun routeToFirstService() {
        val wireMockServer: WireMockServer = WireMockServer(8081)
        wireMockServer.start()

        // given
        wireMockServer
            .stubFor(
                any(urlPathMatching("^(\\/first\\-service\\/)\\w+")).withHeader("first-request", equalTo("first-requests-header2"))
                    .willReturn(aResponse().withStatus(200))
            )

        // when, then
        webTestClient.get().uri("/first-service/message").exchange()
            .expectHeader().valueEquals("first-response", "first-response-header2")
            .expectStatus().isOk

        wireMockServer.shutdown()
    }

    @Test
    fun routeToSecondService() {
        val wireMockServer: WireMockServer = WireMockServer(8082)
        wireMockServer.start()

        wireMockServer
            .stubFor(
                any(urlPathMatching("^(\\/second\\-service\\/)\\w+")).withHeader("second-request", equalTo("second-requests-header2"))
                    .willReturn(aResponse().withStatus(200))
            )

        webTestClient.get().uri("/second-service/message").exchange()
            .expectHeader().valueEquals("second-response", "second-response-header2")
            .expectStatus().isOk

        wireMockServer.shutdown()
    }

}