plugins {
    `java-library`
    jacoco
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val integrationTestImplementation by configurations.creating
val integrationTestRuntimeOnly by configurations.creating

integrationTestImplementation.extendsFrom(configurations.testImplementation.get())
integrationTestRuntimeOnly.extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.6.2"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:2.6.2"))

    implementation("org.slf4j:slf4j-api")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.mockito:mockito-junit-jupiter")
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTest = task<Test>("integrationTest") {
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(integrationTest)
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.7"
}
