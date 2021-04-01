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
import de.articdive.konfig.api.PropertyHolder
import de.articdive.konfig.api.SectionHolder
import de.articdive.konfig.util.PathUtil
import kotlin.reflect.KClass
import kotlin.reflect.cast

class YamlConfigurationSection : ConfigurationSection(), CommentHolder, SectionHolder<YamlConfigurationSection>,
    PropertyHolder<YamlProperty<*>> {
    val sections = HashMap<String, YamlConfigurationSection>()
    val properties = HashMap<String, Property<*>?>()
    var comment = ""

    override fun comment(comment: String) {
        this.comment = comment;
    }

    inline fun <reified T : Any> property(
        propertyName: String,
        defaultValue: T?,
        noinline init: YamlProperty<*>.() -> Unit
    ): YamlProperty<*> {
        return property(propertyName, T::class, defaultValue, init)
    }

    override fun <T : Any> property(
        propertyName: String,
        type: KClass<T>,
        defaultValue: T?,
        init: YamlProperty<*>.() -> Unit
    ): YamlProperty<*> {
        val fPath = PathUtil.formatName(propertyName)

        val property = YamlProperty(type, defaultValue)
        property.init()
        properties[fPath] = property
        return property
    }

    override fun section(sectionName: String, init: YamlConfigurationSection.() -> Unit): YamlConfigurationSection {
        val fSectionName = PathUtil.formatName(sectionName)

        val section = YamlConfigurationSection()
        section.init()
        sections[fSectionName] = section
        return section
    }

    override fun <V : Any> get(propertyPath: String, type: KClass<V>): V? {
        val fPath = PathUtil.formatPath(propertyPath)

        return if (fPath.contains('.')) {
            val propertyName = PathUtil.getPropertyName(fPath)
            val sectionPath = PathUtil.getSectionPath(fPath)
            // Return from that sub-section's property map.
            getSection(sectionPath)?.get(propertyName, type)
        } else {
            // Return from this section's property map.
            type.cast(properties[fPath]!!.value)
        }
    }

    override fun <V : Any> set(propertyPath: String, value: V?): Boolean {
        val fPath = PathUtil.formatPath(propertyPath)

        return if (fPath.contains('.')) {
            val propertyName = PathUtil.getPropertyName(fPath)
            val sectionPath = PathUtil.getSectionPath(fPath)
            // Set to that sub-section's property map.
            getSection(sectionPath)?.set(propertyName, value) ?: false
        } else {
            val property: Property<*> = properties[fPath]!!
            return if (property.type.isInstance(value)) {
                property.unsafeSet(value)
                true
            } else {
                false
            }
        }
    }

    override fun getSection(sectionPath: String): YamlConfigurationSection? {
        val fPath = PathUtil.formatPath(sectionPath)

        return if (fPath.contains('.')) {
            val sectionName = PathUtil.getPropertyName(fPath)
            val sectionPathReduced = PathUtil.getSectionPath(fPath)
            // Return from that sub-section's property map.
            getSection(sectionPathReduced)?.getSection(sectionName)
        } else {
            sections[fPath]
        }
    }

    override fun setSection(sectionPath: String, section: YamlConfigurationSection): Boolean {
        val fPath = PathUtil.formatPath(sectionPath)

        return if (fPath.contains('.')) {
            val propertyName = PathUtil.getPropertyName(fPath)
            val sectionPathReduced = PathUtil.getSectionPath(fPath)
            // Set to that sub-section's property map.
            getSection(sectionPathReduced)?.setSection(propertyName, section) ?: false
        } else {
            sections[fPath] = section
            true
        }
    }
}