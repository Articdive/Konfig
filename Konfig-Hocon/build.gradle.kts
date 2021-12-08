dependencies {
    api(project(":Konfig-Core"))
    implementation("com.typesafe:config:1.4.1")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.articdive"
            artifactId = "konfig-hocon"
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