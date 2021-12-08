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
            name = "krypton-repo"
            url = uri("https://repo.kryptonmc.org")
            credentials {
                username = System.getenv("KRYPTON_REPO_CREDS_USR")
                password = System.getenv("KRYPTON_REPO_CREDS_PSW")
            }
        }
    }
}