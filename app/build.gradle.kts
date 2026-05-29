import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project.dependencies.platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.rest.assured.spring.mock.mvc.kotlin.extensions)
}

tasks.bootBuildImage {
    runImage = "gcr.io/distroless/java-base-debian13:nonroot"
    imageName = "distroless-jvm-examples-distroless-custom-jre-buildpack"
    environment = mapOf(
        "BP_JVM_JLINK_ENABLED" to "true",
        "BP_JVM_JLINK_ARGS" to "--add-modules java.base,java.compiler,java.desktop,java.instrument,java.naming,java.net.http,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.jfr,jdk.management,jdk.unsupported",
    )
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
}
