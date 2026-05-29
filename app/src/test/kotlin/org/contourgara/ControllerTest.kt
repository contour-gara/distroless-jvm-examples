package org.contourgara

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc

@ApplyExtension(SpringExtension::class)
@WebMvcTest
class ControllerTest(val mockMvc: MockMvc,) : FunSpec({
    beforeEach {
        mockMvc(mockMvc)
    }

    test("ルートに GET した場合、文字列で 'Hello World!' が返る") {
        // execute & assert
        Given {
            body("")
        } When {
            get("/")
        } Then {
            status(HttpStatus.OK)
            contentType("text/plain")
            body(equalTo("Hello World!"))
        }
    }
})
