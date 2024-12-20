plugins {
    java
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.project"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-logging")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.12")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.16")
    implementation("com.opencsv:opencsv:5.7.1")

    compileOnly("org.projectlombok:lombok:1.18.24")

    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}