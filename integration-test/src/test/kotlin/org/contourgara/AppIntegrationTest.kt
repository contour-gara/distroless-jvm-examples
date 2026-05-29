package org.contourgara

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withContexts
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo

class AppIntegrationTest : FunSpec({
    context("ルートに GET した場合、文字列で 'Hello World!' が返る") {
        val services = listOf(
            "http://localhost:8081",
            "http://localhost:8082",
            "http://localhost:8083",
            "http://localhost:8084",
        )

        withContexts(services) { service ->
            // execute & assert
            Given {
                baseUri(service)
                body("")
            } When {
                get("/")
            } Then {
                statusCode(200)
                contentType("text/plain")
                body(equalTo("Hello World!"))
            }
        }
    }
})
