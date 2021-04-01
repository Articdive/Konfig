/*
 * Konfig
 * Copyright (C) 2021 Articdive
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

import de.articdive.konfig.HoconConfiguration
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * @author Articdive
 */
class HoconTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun testConfigLoad() {
        val testConfig = HoconConfiguration(
            tempDir.resolve("save.conf"),
            javaClass.getResource("load.conf")
        ) {
            // Normal stuff
            property("boolean", false) {
                comment("Comment")
            }
            property("double", 1.11) {
                comment("Comment")
            }
            property("integer", 1) {
                comment("Comment")
            }
            property("long", 1L) {
                comment("Comment")
            }
            property("list", arrayListOf("Hello", "John", "was", "here!")) {
                comment("Comment")
            }
            // Extreme nesting example
            section("a") {
                comment("Section A")

                property<String>("name", null) {
                    comment("This is a name.")
                }

                section("b") {
                    comment("Section B")

                    property("name", "Tommy") {
                        comment("This is a name.")
                    }

                    section("c") {
                        comment("Section C")

                        property<String>("name", null) {
                            comment("This is a name.")
                        }
                    }

                    section("d") {
                        comment("Section D")

                        property("name", "Samuel") {
                            comment("This is a name.")
                        }
                    }
                }
            }
        }
        val expected: String = try {
            String(Files.readAllBytes(Paths.get(javaClass.getResource("load.conf").toURI())))
        } catch (e: IOException) {
            fail(e.message, e)
        } catch (e: URISyntaxException) {
            fail(e.message, e)
        }
        Assertions.assertAll({
            assertEquals(expected.trim().replace("\r\n", "\n"), testConfig.saveToString().trim().replace("\r\n", "\n"))
            assertEquals(true, testConfig.get("boolean"))
            assertEquals(-1.231, testConfig.get("double"))
            assertEquals(414, testConfig.get("integer"))
            assertEquals(1484, testConfig.get<Long>("long"))
            assertEquals("Jona", testConfig.get("a.name"))
            assertEquals("Tommy", testConfig.get("a.b.name"))
            assertEquals("Jake", testConfig.get("a.b.c.name"))
            assertEquals("Samuel", testConfig.get("a.b.d.name"))
            assertEquals(arrayListOf("Hello", "John", "was not", "here!"), testConfig.get<List<String>>("list"))
        })
    }

    @Test
    fun testConfigSave() {
        val testConfig = HoconConfiguration(tempDir.resolve("save.conf")) {
            property("boolean", false) {
                comment("Comment")
            }
            property("double", 1.11) {
                comment("Comment")
            }
            property("integer", 1) {
                comment("Comment")
            }
            property("long", 1L) {
                comment("Comment")
            }
            property("list", arrayListOf("Hello", "John", "was", "here!")) {
                comment("Comment")
            }
            // Extreme nesting example
            section("a") {
                comment("Section A")

                property<String>("name", null) {
                    comment("This is a name.")
                }

                section("b") {
                    comment("Section B")

                    property("name", "Tommy") {
                        comment("This is a name.")
                    }

                    section("c") {
                        comment("Section C")

                        property<String>("name", null) {
                            comment("This is a name.")
                        }
                    }

                    section("d") {
                        comment("Section D")

                        property("name", "Samuel") {
                            comment("This is a name.")
                        }

                    }
                }
            }
        }

        val output = testConfig.saveToString().trim().replace("\r\n", "\n")
        val expected: String = try {
            String(Files.readAllBytes(Paths.get(javaClass.getResource("save.conf").toURI())))
        } catch (e: IOException) {
            fail(e.message, e)
        } catch (e: URISyntaxException) {
            fail(e.message, e)
        }
        // Hocon outputs in LF and the file is saved in CRLF
        assertEquals(expected.trim().replace("\r\n", "\n"), output)
    }
}