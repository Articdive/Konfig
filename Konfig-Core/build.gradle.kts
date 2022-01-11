plugins {
    id("konfig.publishing-conventions")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.articdive"
            artifactId = "konfig-core"
            version = rootProject.version.toString()
            from(components["java"])

            pom {
                name.set("Konfig-Core")
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