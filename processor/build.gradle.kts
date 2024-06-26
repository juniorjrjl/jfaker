plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "net.jfaker"
version = "1.0.0"

repositories {
    mavenCentral()
}

publishing{
    publications{
        create<MavenPublication>("mavenJava"){
            from(components["java"])

            groupId = "net.jfaker"
            artifactId = "jfaker-processor"
            version = "1.0.0"

            pom{
                name.set("JFaker Processor")
                description.set("Create instances with random data for tests")
                url.set("https://github.com/juniorjrjl/jfaker")

                licenses{
                    license{
                        name.set("")
                        url.set("")
                        distribution.set("")
                    }
                }

                developers {
                    developer{
                        id.set("")
                        name.set("José Luiz Junior")
                        email.set("")
                    }
                }

                scm{
                    connection.set("")
                    developerConnection.set("")
                    url.set("https://github.com/juniorjrjl/jfaker")
                }

            }
        }
    }

    repositories{
        maven {
            name = "sonatype"
            url = uri("")
            credentials {
                username = project.findProperty("") as String?
                password = project.findProperty("") as String?
            }
        }
    }

}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        findProperty("signing.secretKeyRingFile") as String?,
        findProperty("signing.password") as String?
    )
    sign(publishing.publications["mavenJava"])
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
