/*
 * Konfig
 * Copyright (C) 2021-2022 Articdive
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    id("konfig.common-conventions")
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            credentials {
                username = System.getenv()["SONATYPE_USERNAME"] ?: (if (hasProperty("SONATYPE_USERNAME")) (property("SONATYPE_USERNAME") as String) else "")
                password = System.getenv()["SONATYPE_PASSWORD"] ?: (if (hasProperty("SONATYPE_PASSWORD")) (property("SONATYPE_PASSWORD") as String) else "")
            }
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
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