tasks {
    create("stage") {
        dependsOn("clean", "shadowJar", "copyToLib")
    }

    create("copyToLib", Copy::class) {
        val folder = "/libs"
        from("$buildDir$folder")
        into("${rootProject.buildDir}$folder")

        dependsOn("shadowJar")
    }
}