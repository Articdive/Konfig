import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.articdive"
version = "1.0.0"

plugins {
    kotlin("jvm") version ("1.5.10") apply (false)
}


repositories {
    mavenCentral()
    jcenter()
}

subprojects {
    group = "${rootProject.group}"
    version = "${rootProject.version}"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        jcenter()
        // Sonatype Repository
        maven {
            url = uri("https://oss.sonatype.org/content/groups/public/")
        }
    }

    dependencies {
        // Jetbrains annotations
        "compileOnly"("org.jetbrains:annotations:20.1.0")
        "implementation"(kotlin("stdlib-jdk8"))
        // JUnit testing framework
        "testImplementation"(kotlin("test-junit5"))
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.7.0")
        // Include JUnit enginge for runtime.
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                sourceCompatibility = JavaVersion.VERSION_11.toString()
                targetCompatibility = JavaVersion.VERSION_11.toString()
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
