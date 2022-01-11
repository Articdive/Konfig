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
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks {
    withType<JavaCompile> {
        // We are fully aware, that we should be suppressing these instead of ignoring them here, but man keep my terminal clean.
        options.compilerArgs.addAll(listOf("-Xlint:none", "-Xlint:-deprecation", "-Xlint:-unchecked"))
    }
    withType<Test> {
        useJUnitPlatform()
    }
}