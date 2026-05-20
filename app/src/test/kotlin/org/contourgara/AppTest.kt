package org.contourgara

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.boot.test.context.SpringBootTest

@ApplyExtension(SpringExtension::class)
@SpringBootTest
class AppTest(val testBean: TestBean) : FunSpec({
    test("context loads") {
        testBean.shouldNotBeNull()
    }
})
