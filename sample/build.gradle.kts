plugins {
    id("java")
}

group = "net.jfaker"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.datafaker:datafaker:2.2.2")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testAnnotationProcessor(project(":processor"))

    testImplementation(project(":processor"))
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}