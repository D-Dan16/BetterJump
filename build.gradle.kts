@file:Suppress("DEPRECATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.jetbrains.intellij.platform") version "2.10.4"
}

group = "me.dirtydan16"
version = "1.0.1"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  intellijPlatform {
    create("IC", "2025.1")
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

    // Add necessary plugin dependencies for compilation here, example:
    // bundledPlugin("com.intellij.java")
  }
}


intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "251"
    }

    changeNotes = """
      Refactored caret offset widget to support dynamic updates and fixed block traversal action-related bugs. 
      
      Bug fixes include:
      - comments are now ignored in traversal
      - can traverse reliably through the outermost layer of code blocks (which are typically class declarations)
      - fixed unique cases where when you go to the next block, you go to a block that is in a diff layer than the layer the current caret is at.
      - Fixed cases where trying to go to an inner block wouldn't work
    """.trimIndent()
  }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
  }

}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-XXLanguage:+BreakContinueInInlineLambdas", "-XXLanguage:+WhenGuards"))
  freeCompilerArgs.add("-Xwhen-guards")
}