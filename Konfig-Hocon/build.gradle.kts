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
            name = "minestom-repo"
            url = uri("https://repo.minestom.net/repository/maven-public/")
            credentials {
                username = System.getenv("MINESTOM_REPO_CREDS_USR")
                password = System.getenv("MINESTOM_REPO_CREDS_PSW")
            }
        }
    }
}