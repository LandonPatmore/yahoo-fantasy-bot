tasks {
    "clean"(Delete::class) {
        doLast {
            delete(rootProject.buildDir)
        }
    }
}