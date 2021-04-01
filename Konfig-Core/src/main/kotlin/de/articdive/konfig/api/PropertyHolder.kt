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

package de.articdive.konfig.api

import de.articdive.konfig.Property
import kotlin.reflect.KClass

/**
 * @author Articdive
 */
interface PropertyHolder<P : Property<*>> {

    fun <T : Any> property(
        propertyName: String, type: KClass<T>, defaultValue: T?, init: P.() -> Unit = {
        }
    ): P

    fun <V : Any> get(propertyPath: String, type: KClass<V>): V?

    fun <V : Any> set(propertyPath: String, value: V?): Boolean
}