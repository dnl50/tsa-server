plugins {
    `java-convention`
    `spotless-config`
}

dependencies {
    api(project(":domain"))
    api(project(":signing"))

    implementation(project(":persistence"))

    implementation("org.springframework:spring-context")
    implementation("commons-codec:commons-codec")
}
