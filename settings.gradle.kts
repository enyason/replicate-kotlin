plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    id("com.gradle.enterprise") version("3.15")
}
rootProject.name = "replicate-kotlin"

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
