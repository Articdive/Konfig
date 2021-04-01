publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.articdive"
            artifactId = "konfig-core"
            version = "${rootProject.version}"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "minestom-repo"
            url = uri("https://repo.minestom.net/repository/maven-public/")
            credentials {
                username = System.getenv("MINESTOM_REPO_CREDS_USR")
                password = System.getenv("MINESTOM_REPO_CREDS_PSW")
            }
        }
    }
}