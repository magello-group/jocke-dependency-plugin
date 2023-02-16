plugins {
  id("org.jetbrains.intellij") version "1.13.0"
  id("groovy")
  id("idea")
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

group = "org.jocke"
version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
  type.set("IU")
  plugins.set(listOf("Groovy", "gradle", "java", "Kotlin"))
  pluginName.set("jockes-dependency-plugin")
  version.set("223.8617.56")
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
  }
  compileJava {
    java {
      targetCompatibility = JavaVersion.VERSION_17
      sourceCompatibility = JavaVersion.VERSION_17
    }
  }
}
