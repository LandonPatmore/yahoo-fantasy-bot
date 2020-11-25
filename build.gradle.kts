import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.koin:koin-gradle-plugin:2.1.6")
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "4.0.2"
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "koin")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.1.2")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.github.scribejava:scribejava-apis:6.0.0")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    }

    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }

    tasks.withType(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

apply(file("${rootProject.projectDir}/gradle/heroku/clean.gradle.kts"))

project(":bot") {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(file("${rootProject.projectDir}/gradle/heroku/stage.gradle.kts"))
}

project(":backend") {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(file("${rootProject.projectDir}/gradle/heroku/stage.gradle.kts"))
}