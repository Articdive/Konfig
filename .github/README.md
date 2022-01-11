# Konfig

![banner](banner.png)

[![license](https://img.shields.io/github/license/Articdive/Konfig.svg)](../LICENSE)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg)](https://github.com/RichardLitt/standard-readme)
[![Discord Shield](https://discordapp.com/api/guilds/525595722859675648/widget.png?style=shield)](https://discord.gg/c26nC7FxU6)

Konfig is a simplistic Kotlin-library for creating configs using a Kotlin DSL.

Konfig was created in early 2021 by Articdive.  
It works in the JVM 11 (or higher) environment with [Kotlin](https://kotlinlang.org/) apps. In theory, it can also be used from pure Java.
It is built using [Gradle](https://gradle.org/).

## Table of Contents

- [Install](#install)
- [Usage](#usage)
- [Planned Features](#planned-features)
- [Maintainers](#maintainers)
- [Acknowledgements](#acknowledgements)
- [Contributing](#contributing)
- [License](#license)

## Install

### Maven and Gradle

To add Konfig to your project using [Maven](http://maven.apache.org/) or [Gradle](https://gradle.org/):

Adding to a Maven Project:

```xml
<dependencies>
    <dependency>
        <groupId>de.articdive</groupId>
        <artifactId>konfig-TYPE</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

Adding to a Gradle Project (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("de.articdive:konfig-TYPE:VERSION")
}
```

### Config Types

The library currently supports Hocon and Yaml as the configuration types, the artifact IDs are:

```
konfig-hocon
konfig-yaml
```

## Usage

### Creating a config:

You must extend a configuration type depending on the implementation you choose.
> HoconConfiguration
> YamlConfiguration

```kotlin
val myConfig = HoconConfiguration(Path.of("test.hocon")) {
    section("a") {
        comment("This is section A")
        comment("You can add multiple comments!")

        section("b") {
            comment("You can even nest sections!")
        }
    }
    proprety("str", "This is the default value")
    property("d", 4) {
        comment("This is integer D")
    }
}
```

### Defaults

If you have a default file e.g. a language file with translations you can also include a default source URL and it will
automatically copy and load the content from the URL.

An example of this:

```kotlin
val myConfig = HoconConfiguration(Path.of("test.hocon"), myURL) {
}
```

### Accessing a property

#### Getting

To access a property we use a getter. We specify the property's path and type as parameters.
Most of the time Kotlin will automatically infer the type, however it can also be explicitly stated:
```kotlin
val myValue = myConfig.get("Section.Path")
val myExplicitValue = myConfig.get<Type>("Section.Path")
```

#### Setting

We can also change options in the config by using a setter. It is important that the type is the same as specified in your config definition.

```kotlin
var myConfig: HoconConfiguration
myConfig.set("Section.Path", "Hello World!")
```

## Planned Features

Planned features include Toml and Yaml support.

The eo-yaml library is not yet adequately developed to handle Konfig and SnakeYaml does not allow for handling
comments, if you know of any java parsers that support comments processing please open an issue to let us know. Once
some issues have been solved with eo-yaml, Yaml support will be added.

## Maintainers

[@Articdive](https://www.github.com/Articdive/)

## Acknowledgements

[@Typesafe Config](https://github.com/lightbend/config)

[@eo-yaml](https://github.com/decorators-squad/eo-yaml)

## Contributing

See [the contributing file](CONTRIBUTING.md)!

## License

[GNU General Public License v3.0 or later © Articdive ](../LICENSE)