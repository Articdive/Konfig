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

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigOriginFactory
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueFactory
import de.articdive.konfig.exceptions.ConfigSaveException
import de.articdive.konfig.util.PathUtil
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.reflect.KClass

/**
 * @author Articdive
 */
class HoconConfiguration(
    path: Path,
    defaultResourceURL: URL? = null,
    init: HoconConfigurationSection.() -> Unit
) :
    Configuration<HoconConfigurationSection>(
        path,
        defaultResourceURL,
        HoconConfigurationSection(),
        init
    ) {
    private lateinit var config: Config

    init {
        load()
        save()
    }

    override fun load() {
        config = ConfigFactory.parseFile(path.toFile())
        loadSection("", mainSection)
    }

    private fun loadSection(path: String, section: HoconConfigurationSection) {
        val sectionPath = PathUtil.formatPath(path)

        // Properties
        section.properties.forEach { propertyEntry ->
            // The value of the propertyEntry cannot be null since we ALWAYS use a Property class.
            // Even if the user sets it to null it has a type (and a property object).
            val property: HoconProperty<*> = propertyEntry.value!! as HoconProperty<*>
            val propertyType = property.type
            val propertyPath = PathUtil.formatPath("$sectionPath.${propertyEntry.key}")

            // Get config value
            var configValue: ConfigValue? = null
            if (config.hasPathOrNull(propertyPath)) {
                configValue = if (config.getIsNull(propertyPath)) {
                    ConfigValueFactory.fromAnyRef(null)
                } else {
                    config.getValue(propertyPath)
                }
            }
            // Check if that location is not defined.
            // If it were defined configValue != null.
            if (configValue == null) {
                return@forEach
            }

            // These edge cases are hard to handle:
            // As an example 2 can be a byte, short, int or long.
            // And 2.0 can be a float and a decimal.
            // That is why the property type is important
            // The else clause ensures that everything else is set smoothly
            when (propertyType) {
                Byte::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toByte())
                }
                Short::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toShort())
                }
                Int::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toInt())
                }
                Long::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toLong())
                }
                Float::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toFloat())
                }
                Double::class -> {
                    property.unsafeSet(configValue.unwrapped().toString().toDouble())
                }
                else -> {
                    if (propertyType.isInstance(configValue.unwrapped())) {
                        property.unsafeSet(configValue.unwrapped())
                    }
                }
            }
        }
        // Call the next layer of sections
        section.sections.forEach { (key, value) ->
            loadSection("$sectionPath.$key", value)
        }
    }

    override fun save() {
        saveSection("", mainSection)

        // Output to file
        try {
            path.writeText(
                config.root().render(
                    ConfigRenderOptions.defaults()
                        .setComments(true).setFormatted(true).setOriginComments(false).setJson(true)
                ),
                StandardCharsets.UTF_8
            )
        } catch (e: IOException) {
            throw ConfigSaveException("Failed to save configuration to file.", e)
        }
    }

    fun saveToString(): String {
        return config.root().render(
            ConfigRenderOptions.defaults()
                .setComments(true).setFormatted(true).setOriginComments(false).setJson(true)
        )
    }

    private fun saveSection(path: String, section: HoconConfigurationSection) {
        val sectionPath = PathUtil.formatPath(path)

        // Save section itself (we have to do this for the comments on sections!)
        // Edge case (root config)
        if (sectionPath.isNotEmpty()) {

            // Get the comments for the section
            config = config.withValue(
                sectionPath,
                try {
                    ConfigValueFactory.fromAnyRef(config.getObject(sectionPath))
                        .withOrigin(config.getConfig(sectionPath).origin())
                } catch (e: ConfigException) {
                    ConfigValueFactory.fromMap(HashMap())
                        .withOrigin(ConfigOriginFactory.newSimple().withComments(section.comments))
                }
            )
        }
        // Properties
        section.properties.forEach { propertyEntry ->
            // The value of the propertyEntry cannot be null since we ALWAYS use a Property class.
            // Even if the user sets it to null it has a type (and a property object).
            val property = propertyEntry.value!! as HoconProperty<*>
            val propertyPath = PathUtil.formatPath("$sectionPath.${propertyEntry.key}")

            // Edge case
            if (propertyPath.isEmpty()) {
                return@forEach
            }

            // Save property to config
            config = config.withValue(
                propertyPath,
                ConfigValueFactory.fromAnyRef(property.value)
                    .withOrigin(
                        try {
                            config.getValue(propertyPath).origin()
                        } catch (e: ConfigException) {
                            ConfigOriginFactory.newSimple().withComments(property.comments)
                        }
                    )
            )
        }
        // Recursively call the next layer of sections
        section.sections.forEach { (key, value) ->
            saveSection("$sectionPath.$key", value)
        }
    }

    inline fun <reified V : Any> get(propertyPath: String): V? {
        return get(propertyPath, V::class)
    }

    fun <V : Any> get(propertyPath: String, type: KClass<V>): V? {
        return mainSection.get(propertyPath, type)
    }

    inline fun <reified V : Any> set(propertyPath: String): Boolean {
        return set(propertyPath, V::class)
    }

    fun <V : Any> set(propertyPath: String, value: V?): Boolean {
        return mainSection.set(propertyPath, value)
    }

    fun getSection(sectionPath: String): HoconConfigurationSection? {
        return mainSection.getSection(sectionPath)
    }

    fun setSection(sectionPath: String, section: HoconConfigurationSection): Boolean {
        return mainSection.setSection(sectionPath, section)
    }
}