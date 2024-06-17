plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("io.codearte.nexus-staging") version "0.30.0"
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.enyason"
version = "1.0-SNAPSHOT"
val isSnapShotVersion = version.toString().endsWith("SNAPSHOT")

repositories {
    mavenCentral()
    if (isSnapShotVersion) {
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}

dependencies {
    // Retrofit and OkHttp3
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            val releaseRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (isSnapShotVersion) snapshotRepoUrl else releaseRepoUrl)
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "replicate-kotlin"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Replicate Kotlin Client")
                packaging = "jar"
                description.set("Kotlin client for Replicate")
                url.set("https://github.com/enyason/replicate-kotlin")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("Lamouresparus")
                        name.set("Love Otudor")
                        email.set("") // Todo
                    }
                    developer {
                        id.set("Enyason")
                        name.set("Enya Emmanuel")
                        email.set("") // Todo
                    }
                    developer {
                        id.set("MayorJay")
                        name.set("Joseph Olugbohunmi")
                        email.set("joseolu4gsm@yahoo.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/enyason/replicate-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/enyason/replicate-kotlin.git")
                    url.set("https://github.com/enyason/replicate-kotlin/tree/main")
                }
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava8Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

signing {
    val signingKey = System.getenv("MAVEN_GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("MAVEN_GPG_PASSPHRASE")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

nexusStaging {
    serverUrl =
        if (isSnapShotVersion) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://s01.oss.sonatype.org/service/local/"
        }
    username = System.getenv("MAVEN_USERNAME")
    password = System.getenv("MAVEN_PASSWORD")
}
