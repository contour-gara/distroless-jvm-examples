import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.docker.compose)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.rest.assured.kotlin.extensions)
}

dockerCompose {
    useComposeFiles = listOf("../compose.yaml")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )

        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    mustRunAfter("composeUp")
}

tasks.composeDown {
    mustRunAfter("test")
}

tasks.register("integrationTest") {
    dependsOn("composeUp")
    dependsOn("test")
    dependsOn("composeDown")
}
