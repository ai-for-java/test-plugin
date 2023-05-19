plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
    type.set("IC") // TODO IU

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    dependencies {
        implementation("dev.ai4j:ai4j:0.2.0-SNAPSHOT")
        implementation("ch.qos.logback:logback-classic:1.3.7")
        implementation("org.projectlombok:lombok:1.18.20")
        implementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        implementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
        implementation("org.junit.jupiter:junit-jupiter:5.9.3")
        implementation("org.junit.platform:junit-platform-launcher:1.9.3")
        implementation(group = "com.github.adedayo.intellij.sdk", name = "java-psi-api", version = "142.1")
    }
}
