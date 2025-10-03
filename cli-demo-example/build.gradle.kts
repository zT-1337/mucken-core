plugins {
    id("java")
    application
}

group = "de.tzerr"
version = "0.1.0-SNAPSHOT"

application {
    mainClass = "de.tzerr.cli.demo.example.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "de.tzerr.cli.demo.example.Main"
    }
}
