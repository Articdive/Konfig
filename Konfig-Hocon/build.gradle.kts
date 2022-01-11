plugins {
    id("konfig.publishing-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":Konfig-Core"))
    implementation("com.typesafe:config:1.4.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.articdive"
            artifactId = "konfig-hocon"
            version = rootProject.version.toString()
            from(components["java"])

            pom {
                name.set("Konfig-Hocon")
                description.set(rootProject.description)
                url.set("https://github.com/Articdive/Konfig")
                licenses {
                    license {
                        name.set("GPL-3.0 License")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("Articdive")
                        name.set("Articdive")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/Articdive/Konfig.git")
                    developerConnection.set("scm:git:ssh://github.com/Articdive/Konfig.git")
                    url.set("https://github.com/Articdive/Konfig/tree/main")
                }
            }
        }
    }
}

signing {
    if (System.getenv()["CI"] != null) {
        useInMemoryPgpKeys(System.getenv()["SIGNING_KEY"], System.getenv()["SIGNING_PASSWORD"])
        // Only attempt to sign if we are in the CI.
        // If you are publishing to maven local then it doesn't need signing.
        sign(publishing.publications["maven"])
    }
}