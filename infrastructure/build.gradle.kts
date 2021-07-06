import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":hello-api"))

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.31")

    implementation("software.amazon.awscdk:core:1.111.0")
    implementation("software.amazon.awscdk:apigateway:1.111.0")
    implementation("software.amazon.awscdk:lambda:1.111.0")

}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    jar {
        manifest {
            attributes["Main-Class"] = "no.dnb.awsintro.infrastructure.MainApp"
        }

        from(configurations.compileClasspath.get().map{ if (it.isDirectory) it else zipTree(it)})
            //configurations.compile.collect { it.isDirectory() ? it : zipTree(it) }

        dependsOn(":hello-api:shadowJar")
    }
}
