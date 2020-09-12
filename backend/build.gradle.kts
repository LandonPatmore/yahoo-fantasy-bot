import com.moowork.gradle.node.task.NodeTask

val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    application
    id("com.moowork.node") version "1.3.1"
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

node {
    nodeModulesDir = file("frontend")
}

repositories {
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.create<Delete>("cleanFrontend") {
    delete("resources/frontend")
}

tasks.create<Copy>("copyFrontend") {
    dependsOn("cleanFrontend")
    from(file("frontend/build"))
    into(file("resources/frontend"))
}

tasks.create<com.moowork.gradle.node.npm.NpmTask>("buildFrontend") {
    dependsOn("npmInstall")
    setArgs(listOf("run", "build"))
    finalizedBy("copyFrontend")
}
