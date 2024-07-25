import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.enyason"
version = "0.1.0"

repositories {
    mavenCentral()
}

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "io.github.enyason",
        artifactId = "replicate-kotlin",
        version = version.toString(),
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Replicate-Kotlin")
        description.set(
            "Replicate-Kotlin is a wrapper around Replicate’s API, enabling you to interact " +
                    "with cloud-based AI models using pure Kotlin code. " +
                    "This library is designed to easily integrate generative AI into " +
                    "Android and other kotlin supported environments.",
        )
        inceptionYear.set("2024")
        url.set("https://github.com/enyason/replicate-kotlin")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        // Specify developer information
        developers {
            developer {
                id.set("enyason")
                name.set("Emmanuel Enya")
                email.set("enyason@gmail.com")
            }

            developer {
                id.set("mayorJAY")
                name.set("Joseph Olugbohunmi")
                email.set("joseolu4gsm@yahoo.com")
            }

            developer {
                id.set("Lamouresparus")
                name.set("Love Otudor")
                email.set("loveotudor@gmail.com")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/enyason/replicate-kotlin")
            connection.set("scm:git:git://github.com/enyason/replicate-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com/enyason/replicate-kotlin.git")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    // Enable GPG signing for all publications
    signAllPublications()
}

dependencies {
    // Retrofit and OkHttp3
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.9.3")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
