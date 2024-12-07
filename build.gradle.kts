plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // JUnit dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0")) // JUnit BOM for consistent versions
    testImplementation("org.junit.jupiter:junit-jupiter")

    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:5.6.0")

    // add other dependencies here as needed
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Adjust Java version if necessary
    }
}
