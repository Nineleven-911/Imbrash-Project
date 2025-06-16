fun projectModule(name: String) {
    dependencies {
        implementation(project(name))
    }
}

plugins {
    kotlin("jvm")
}

group = "hairinne.ip.vm"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    projectModule(":utils")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
