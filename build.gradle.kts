plugins {
    id("org.sonarqube") version "5.0.0.4638"
    id("java")
    jacoco
}

group = "net.jfaker"
version = "0.0.1"

subprojects {
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = "0.8.8"
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.named("test") })

    additionalSourceDirs.setFrom(files(subprojects.flatMap { it.the<SourceSetContainer>().named("main").get().allSource.srcDirs }))
    sourceDirectories.setFrom(files(subprojects.flatMap { it.the<SourceSetContainer>().named("main").get().allSource.srcDirs }))
    classDirectories.setFrom(files(subprojects.flatMap { it.the<SourceSetContainer>().named("main").get().output }))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
    }
}

repositories {
    mavenCentral()
}

