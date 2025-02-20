plugins {
    `java-library`
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "org.jaibf"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.runServer {
    minecraftVersion("1.21.4")
}