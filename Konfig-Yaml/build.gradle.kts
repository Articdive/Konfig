dependencies {
    api(project(":Konfig-Core"))
    implementation("com.amihaiemil.web:eo-yaml:5.2.1")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.articdive"
            artifactId = "konfig-yaml"
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