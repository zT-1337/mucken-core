plugins {
    id("java-library")
    id("io.freefair.lombok") version "9.0.0"
    id("maven-publish")
}

group = "de.tzerr"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            pom {
                name = "Mucken Core"
                description = "Java library to play the german card game 'Mucken'."
                url = "https://github.com/zT-1337/mucken-core"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/zT-1337/mucken-core/blob/main/LICENSE.txt"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:zT-1337/mucken-core.git"
                    url = "https://github.com/zT-1337/mucken-core"
                }
            }
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.6.1")
}

tasks.test {
    useJUnitPlatform()
}