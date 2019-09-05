plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib"))
    implementation("com.github.scribejava:scribejava-apis:6.0.0")
    implementation("org.postgresql:postgresql:42.2.5")
}
