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

import de.articdive.konfig.api.CommentHolder
import kotlin.reflect.KClass


class YamlProperty<T : Any>(type: KClass<T>, defaultValue: T?) : Property<T>(type, defaultValue), CommentHolder {
    var comment = ""

    override fun comment(comment: String) {
        this.comment = comment
    }
}