plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")

    //NBT library
    implementation("com.github.Querz:NBT:6.1")

    //Guava
    implementation("com.google.guava:guava:33.6.0-jre")

    //Lombok
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        minecraftVersion("26.1.2")
        jvmArgs("-Xms1G", "-Xmx1G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version , "description" to project.description )
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
