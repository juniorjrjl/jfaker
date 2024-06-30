plugins {
    id("java")
    id("jacoco")
}

group = "net.jfaker"
version = "1.0.0"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    additionalSourceDirs.setFrom(files(project(":processor").the<SourceSetContainer>().named("main").get().allSource.srcDirs))
    sourceDirectories.setFrom(files(project(":processor").the<SourceSetContainer>().named("main").get().allSource.srcDirs))
    classDirectories.setFrom(files(project(":processor").the<SourceSetContainer>().named("main").get().output))
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)

    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(files(project(":processor").the<SourceSetContainer>().named("main").get().output))
    executionData.setFrom(files(project(":processor").layout.buildDirectory.file("jacoco/test.exec").get().asFile))
}

dependencies {
    implementation("net.datafaker:datafaker:2.2.2")
    implementation(project(":processor"))

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testAnnotationProcessor(project(":processor"))

    testImplementation(project(":processor"))
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("com.google.guava:guava:33.2.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.0")
    testImplementation("org.apache.commons:commons-lang3:3.14.0")


}
