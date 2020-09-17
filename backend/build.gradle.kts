import com.moowork.gradle.node.task.NodeTask

val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    application
    id("com.moowork.node") version "1.3.1"
}

application {
    mainClassName = "com.landonpatmore.yahoofantasybot.backend.ApplicationKt"
}

node {
    nodeModulesDir = file("../frontend")
    version = "14.5.0"
    download = true
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
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("org.koin:koin-ktor:2.1.6")
    implementation(project(":shared"))
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
    from(file("../frontend/build"))
    into(file("resources/frontend"))
}

tasks.create<com.moowork.gradle.node.npm.NpmTask>("buildFrontend") {
    dependsOn("npmInstall")
    setArgs(listOf("run", "build"))
    finalizedBy("copyFrontend")
}

tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    dependsOn("buildFrontend")
}
