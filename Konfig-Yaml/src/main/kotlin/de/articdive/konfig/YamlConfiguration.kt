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

import com.amihaiemil.eoyaml.Node
import com.amihaiemil.eoyaml.Yaml
import com.amihaiemil.eoyaml.YamlMapping
import com.amihaiemil.eoyaml.YamlMappingBuilder
import com.amihaiemil.eoyaml.YamlNode
import de.articdive.konfig.exceptions.ConfigSaveException
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.writer
import kotlin.reflect.KClass

/**
 * @author Articdive
 */
class YamlConfiguration(
    path: Path,
    defaultResourceURL: URL? = null,
    init: YamlConfigurationSection.() -> Unit
) :
    Configuration<YamlConfigurationSection>(
        path,
        defaultResourceURL,
        YamlConfigurationSection(),
        init
    ) {
    private lateinit var config: YamlMapping

    init {
        load()
        save()
    }

    override fun load() {
        config = Yaml.createYamlInput(path.inputStream(), true).readYamlMapping()
        // Get config value
        loadSection(mainSection, config)
    }

    private fun loadSection(section: YamlConfigurationSection, mapping: YamlMapping) {

        // Load section comment
        section.comment = mapping.comment().value()

        // Properties
        section.properties.forEach { propertyEntry ->
            // The value of the propertyEntry cannot be null since we ALWAYS use a Property class.
            // Even if the user sets it to null it has a type (and a property object).
            val property: YamlProperty<*> = propertyEntry.value!! as YamlProperty<*>
            val propertyType = property.type


            // Check if that location is not defined.
            // If it were defined configValue != null.
            val configValue: YamlNode = mapping.value(propertyEntry.key) ?: return@forEach
            // NOTE: This does not mean that YamlNode's VALUE is not null!
            // It just means that the YamlNode itself does not exist.

            // Set the comment
            property.comment = configValue.comment().value()

            when (propertyType) {
                Boolean::class -> {
                    property.unsafeSet(configValue.asScalar().value().toBoolean())
                }
                Byte::class -> {
                    property.unsafeSet(configValue.asScalar().value().toByte())
                }
                Short::class -> {
                    property.unsafeSet(configValue.asScalar().value().toShort())
                }
                Int::class -> {
                    property.unsafeSet(configValue.asScalar().value().toInt())
                }
                Long::class -> {
                    property.unsafeSet(configValue.asScalar().value().toLong())
                }
                Float::class -> {
                    property.unsafeSet(configValue.asScalar().value().toFloat())
                }
                Double::class -> {
                    property.unsafeSet(configValue.asScalar().value().toDouble())
                }
                String::class -> {
                    property.unsafeSet(configValue.asScalar().value())
                }
                else -> {
                    // List
                    if (configValue.type() == Node.SEQUENCE) {
                        if (List::class.java.isAssignableFrom(propertyType.java)) {
                            property.unsafeSet(configValue.asSequence().map { node -> node.asScalar().value() }
                                .toList())
                        }
                    }
                    // Custom types
                    if (YamlNode::class.java.isAssignableFrom(property.type.java)) {
                        property.unsafeSet(
                            configValue.asClass(
                                propertyType.java.asSubclass(YamlNode::class.java),
                                configValue.type()
                            )
                        )
                    }

                }
            }
        }
        // Call the next layer of sections
        section.sections.forEach { (key, value) ->
            val subMapping = mapping.yamlMapping(key)
            if (subMapping != null) {
                loadSection(value, subMapping)
            }
        }
    }

    override fun save() {
        val ym = saveSection(mainSection, Yaml.createYamlMappingBuilder())

        // Output to file
        try {
            Yaml.createYamlPrinter(path.writer(StandardCharsets.UTF_8)).print(ym)
        } catch (e: IOException) {
            throw ConfigSaveException("Failed to save configuration to file.", e)
        }
    }

    private fun saveSection(
        section: YamlConfigurationSection,
        yamlBuilder: YamlMappingBuilder
    ): YamlMapping {

        var builder = yamlBuilder
        // Properties
        section.properties.forEach { propertyEntry ->
            // The value of the propertyEntry cannot be null since we ALWAYS use a Property class.
            // Even if the user sets it to null it has a type (and a property object).
            val property = propertyEntry.value!! as YamlProperty<*>

            // Save property to config
            builder = builder.add(
                propertyEntry.key, toYamlNode(property)
            )
        }
        // Recursively call the next layer of sections
        section.sections.forEach { (key, value) ->
            builder = builder.add(key, saveSection(value, Yaml.createYamlMappingBuilder()))
        }
        // Section comment
        return builder.build(section.comment)
    }

    private fun toYamlNode(property: YamlProperty<*>): YamlNode? {
        if (property.value == null) {
            return Yaml.createYamlScalarBuilder().addLine(null).buildPlainScalar(property.comment, "")
        }
        when (property.type) {
            Boolean::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Boolean).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Byte::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Byte).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Short::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Short).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Int::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Int).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Long::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Long).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Float::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Float).toString())
                    .buildPlainScalar(property.comment, "")
            }
            Double::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as Double).toString())
                    .buildPlainScalar(property.comment, "")
            }
            String::class -> {
                return Yaml.createYamlScalarBuilder().addLine((property.value as String))
                    .buildPlainScalar(property.comment, "")
            }
            else -> {
                // List
                if (List::class.java.isAssignableFrom(property.type.java)) {
                    var builder = Yaml.createYamlSequenceBuilder()
                    for (any in (property.value as List<*>)) {
                        builder = if (any is YamlNode) {
                            builder.add(any)
                        } else {
                            builder.add(any.toString())
                        }
                    }
                    return builder.build(property.comment)
                }
                // Custom types
                if (YamlNode::class.java.isAssignableFrom(property.type.java)) {
                    return property.value as YamlNode
                }
                throw IllegalArgumentException("Could not map element of type ${property.type.simpleName}.")
            }
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

    fun getSection(sectionPath: String): YamlConfigurationSection? {
        return mainSection.getSection(sectionPath)
    }

    fun setSection(sectionPath: String, section: YamlConfigurationSection): Boolean {
        return mainSection.setSection(sectionPath, section)
    }
}