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

package de.articdive.konfig

import de.articdive.konfig.exceptions.ConfigCreationException
import de.articdive.konfig.util.FileUtil.checkOrCreateFile
import java.io.IOException
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.copyTo

/**
 * @author Articdive
 */
abstract class Configuration<T : ConfigurationSection>(
    protected var path: Path,
    private var defaultResourceURL: URL?,
    protected val mainSection: T,
    init: T.() -> Unit,
) {

    init {
        init(mainSection)

        handleFileCreation()
    }

    private fun handleFileCreation() {
        // Check if file exists
        try {
            checkOrCreateFile(path)
        } catch (e: IOException) {
            throw ConfigCreationException("Failed to create file at path ${path.absolute()}.", e)
        }
        // Copy default Resource.
        if (defaultResourceURL != null) {
            try {
                Path.of(defaultResourceURL!!.toURI()).copyTo(path, overwrite = true)
            } catch (e: IOException) {
                throw ConfigCreationException("Failed to copy default resource to the save file.", e)
            }
        }
    }

    protected abstract fun load()

    abstract fun save()
}