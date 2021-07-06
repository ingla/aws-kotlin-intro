import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"

}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))


    // AWS
    //implementation(Dependency.awsLambda("aws-lambda-java-core"))
    //implementation(Dependency.awsEvents("aws-lambda-java-events"))
    //implementation(Dependency.awsSdk("kms"))
    //implementation(Dependency.awsSdk("ssm"))

    // Http4k
    implementation("org.http4k:http4k-core:4.9.9.0")
    implementation("org.http4k:http4k-serverless-lambda:4.9.9.0")
    implementation("org.http4k:http4k-client-apache:4.9.9.0")

    implementation("org.http4k:http4k-format-kotlinx-serialization:4.9.9.0")

    // JWT
    implementation("com.nimbusds:nimbus-jose-jwt:9.10")

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.31")
    runtimeOnly("org.slf4j:jcl-over-slf4j:1.7.31") // for ApacheClient using JCL
    runtimeOnly("org.slf4j:log4j-over-slf4j:1.7.31") // for AWS Lambda using Log4j
    runtimeOnly("org.jlib:jlib-awslambda-logback:1.0.0")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation(kotlin("test-junit5"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        from(test) // Will run unit tests before packaging the jar file.
        archiveFileName.set("production-aws.jar")
    }
}
