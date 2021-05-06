![Gradle Build](https://github.com/Viido/WarriorSim-Classic/actions/workflows/gradle.yml/badge.svg)
# WarriorSim Classic
This is an event-driven simulator for Classic World of Warcraft Warrior class written in Java. 

## Installation
Windows/Linux/macOS: Download the respective installer from [releases](https://github.com/Viido/WarriorSim-Classic/releases).

## Features
* Talent calculator
* World buffs and consumables
* Items and enchants
* Rotation configuration
* Multi-target fights
* Average DPS

## Future features
These features are being worked on:
* Multiple character setups
* Tanking and threat simulation
* Stat weights
* Detailed simulation breakdown
* Support for Burning Crusade expansion

## Setup 
Run project with Gradle. JDK version must be 11+, 16 is not support by this gradle version.
```
git clone https://github.com/Viido/WarriorSim-Classic.git
./gradlew run
```

Create runtime image
```
./gradlew jlink
```

Create installer
```
./gradlew jpackage
```

On windows, use gradlew.bat instead.

## Tools used
[OpenJFX](https://github.com/openjdk/jfx)

[JFoenix 9](https://github.com/jfoenixadmin/JFoenix)

[Log4j2](https://github.com/apache/logging-log4j2)

[JavaFX Gradle Plugin](https://github.com/openjfx/javafx-gradle-plugin)

[Gradle](https://github.com/gradle/gradle)

[Badass JLink Plugin](https://github.com/beryx/badass-jlink-plugin)

[JUnit5](https://github.com/junit-team/junit5)

[Gson](https://github.com/google/gson)
