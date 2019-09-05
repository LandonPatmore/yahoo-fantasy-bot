plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "webserver.Server"
}

dependencies {
    compile(project(":shared"))
    compile(kotlin("stdlib"))
    implementation("com.github.scribejava:scribejava-apis:6.0.0")
    implementation("com.sparkjava:spark-core:2.7.2")
}
