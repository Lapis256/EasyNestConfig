import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    id("java-library")
    id("idea")
    id("maven-publish")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
}

val modId = Constants.Mod.id
val mcVersion: String = libs.versions.minecraft.get()
val jdkVersion = 21

val exportMixin = true

base {
    archivesName = "${project.name}-$mcVersion"
    version = Constants.Mod.version
    group = Constants.Mod.group
}

neoForge {
    version = libs.versions.neoforge.get()

    validateAccessTransformers = true

    file("src/main/resources/META-INF/accesstransformer.cfg").takeIf(File::exists)?.let {
        println("Adding access transformer: $it")
        setAccessTransformers(it)
    }

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = mcVersion
    }

    runs {
        create("client") {
            client()
            gameDirectory.set(file("run"))
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("server") {
            server()
            gameDirectory.set(file("run-server"))
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("data") {
            data()
            gameDirectory.set(file("run-data"))
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    implementation(libs.kotlinForForge)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = jdkVersion
    }

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(jdkVersion)
        }
        JavaVersion.toVersion(jdkVersion).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    processResources {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
    }

    jar {
        manifest {
            attributes(
                "Specification-Title" to Constants.Mod.name,
                "Specification-Vendor" to Constants.Mod.author,
                "Specification-Version" to version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to Constants.Mod.author,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to mcVersion,
                "FMLModType" to "GAMELIBRARY"
            )
        }
    }

    named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
    }

    named<Wrapper>("wrapper").configure {
        distributionType = Wrapper.DistributionType.BIN
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
        maven {
            name = "R2"
            url = uri("s3://maven")
            credentials(AwsCredentials::class) {
                accessKey = (project.findProperty("r2_access_key") ?: System.getenv("R2_ACCESS_KEY") ?: error("R2 access key is missing.")).toString()
                secretKey = (project.findProperty("r2_secret_key") ?: System.getenv("R2_SECRET_KEY") ?: error("R2 secret key is missing.")).toString()
            }
        }
    }
}
