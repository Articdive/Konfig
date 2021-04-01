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

package de.articdive.konfig.util

import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

/**
 * @author Articdive
 */
object FileUtil {
    /**
     * This checks if a directory exists, if it does not it then creates it and if necessary its parents.
     *
     * @param path The directory to ensure the existance of.
     * @return true if the directory exists/was created.
     */
    private fun checkOrCreateDirectory(path: Path) {
        // If the file already exists OR it is successfully created
        if (path.exists()) {
            return
        }
        path.createDirectories()
    }

    /**
     * This checks if a file exists, if it does not it then creates it and if necessary its parents.
     *
     * @param path The path to ensure the existance of.
     * @return true if the file exists/was created.
     * @throws IOException if it fails to create the file.
     */
    @Throws(IOException::class)
    fun checkOrCreateFile(path: Path) {
        // If the file already exists
        if (path.exists()) {
            return
        }
        // File does not exist
        // Check if parent exists
        if (path.parent == null) {
            // The parent directory does not exist, the file is on the root directory, create the file.
            path.createFile()
        } else {
            // The parent directory should exist, create the file.
            checkOrCreateDirectory(path.parent)
            path.createFile()
        }

    }
}