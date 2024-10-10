plugins {
    kotlin("jvm") version "1.9.10" // Plugin de Kotlin actualizado
    id("net.serenity-bdd.serenity-gradle-plugin") version "3.9.0" // Plugin de Serenity actualizado
    id("java")
}

repositories {
    mavenCentral() // Repositorio Maven Central
}

dependencies {
    // Librería estándar de Kotlin
    implementation(kotlin("stdlib"))

    // Dependencias para pruebas con JUnit 4
    testImplementation("junit:junit:4.13.2") // API de JUnit 4

    // Dependencias de Serenity con integración JUnit
    testImplementation("net.serenity-bdd:serenity-core:3.9.0") // Núcleo de Serenity
    testImplementation("net.serenity-bdd:serenity-junit:3.9.0") // Integración de Serenity con JUnit
    testImplementation("net.serenity-bdd:serenity-rest-assured:3.9.0") // Integración de Serenity con Rest-Assured
    testImplementation("io.rest-assured:rest-assured:4.5.1") // Librería de Rest-Assured para pruebas de API

    // Dependencias adicionales de Serenity para screenplay y REST
    testImplementation("net.serenity-bdd:serenity-screenplay:3.9.0") // Integración de screenplay (útil para @Step)
    testImplementation("net.serenity-bdd:serenity-screenplay-rest:3.9.0") // Integración de screenplay con REST

    // Dependencias adicionales
    testImplementation("org.springframework:spring-web:5.3.20") // Librería Spring para manejo de HttpStatus
}

tasks {
    test {
        useJUnit() // Cambiamos a JUnit 4 para la integración con Serenity
        systemProperty("webdriver.driver", "chrome") // Configuración para que Serenity detecte el navegador
        outputs.upToDateWhen { false } // Forzar siempre la generación de reportes nuevos
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}