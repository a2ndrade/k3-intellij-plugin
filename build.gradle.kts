import org.jetbrains.intellij.platform.gradle.TestFrameworkType

buildscript {
  repositories {
    mavenCentral()
  }
}

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    intellijIdeaCommunity("2024.1")
    instrumentationTools()
    bundledPlugin("com.intellij.java")
    testFramework(TestFrameworkType.Platform)
  }
}

plugins {
  id("java")
  id("org.jetbrains.intellij.platform") version "2.0.0-beta8"
  id("org.jetbrains.grammarkit") version "2022.3.2.2"
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

intellijPlatform {
  pluginConfiguration {
    version = "1.5"
    name = "k"
  }
  buildSearchableOptions = false
}

grammarKit {
  jflexRelease = "1.9.2"
}

sourceSets {
  main {
    java.srcDir("gen")
    resources.srcDir("src/main/resources")
  }
}

tasks {
  generateLexer {
    sourceFile.set(file("src/main/resources/com/appian/intellij/k3/k.flex"))
    targetOutputDir.set(file("gen/com/appian/intellij/k3"))
    purgeOldFiles.set(true)
  }
  generateParser {
    sourceFile.set(file("src/main/resources/com/appian/intellij/k3/k.bnf"))
    targetRootOutputDir.set(file("gen"))
    pathToParser.set("com/appian/intellij/k3/parser/KParser.java")
    pathToPsiRoot.set("com/appian/intellij/k3/psi")
    purgeOldFiles.set(true)
  }
  processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }
  compileJava {
    dependsOn(generateLexer, generateParser)
  }
}