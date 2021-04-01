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

import de.articdive.konfig.exceptions.InvalidPathException
import java.util.regex.Pattern

/**
 * @author Articdive
 */
object PathUtil {
    private val VALID_NAME: Regex = Pattern.compile("[a-z_]*").toRegex()
    private val VALID_PATH: Regex = Pattern.compile("[a-z_.]*").toRegex()
    private val MULTIPLE_DOTS: Regex = Pattern.compile("(\\.{2,})").toRegex()

    fun formatName(sectionName: String): String {
        // Lowercase.
        val sectionNameFormatted: String = sectionName.lowercase()

        // Invalid Character check.
        if (!sectionNameFormatted.matches(VALID_NAME)) {
            throw InvalidPathException("Section name $sectionNameFormatted does not match \"[a-z_]*\".")
        }

        return sectionNameFormatted
    }

    fun formatPath(pathName: String): String {
        // Lowercase.
        var pathNameFormatted: String = pathName.lowercase()

        // Invalid Character check.
        if (!pathNameFormatted.matches(VALID_PATH)) {
            throw InvalidPathException("Path $pathNameFormatted does not match \"[a-z_.]*\".")
        }

        // Invalid seperators.
        if (pathNameFormatted.startsWith('.')) {
            pathNameFormatted = pathNameFormatted.drop(1)
        }
        if (pathNameFormatted.endsWith('.')) {
            pathNameFormatted = pathNameFormatted.dropLast(1)
        }
        pathNameFormatted = pathNameFormatted.replace(MULTIPLE_DOTS, ".")

        return pathNameFormatted
    }

    fun getPropertyName(fullPath: String): String {
        return fullPath.substringAfterLast('.')
//        val m = PROPERTY_KEY_NAME.matcher(path);
//        if (m.find()) {
//            return m.group(1)
//        } else {
//            // Thrown when our regex doesn't get the last string after the '.' separator.
//            throw IllegalStateException("Something went wrong!")
//        }
    }

    fun getSectionPath(fullPath: String): String {
        return fullPath.substringBeforeLast('.')
    }
}

