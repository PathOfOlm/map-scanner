plugins {
    id("fabric-loom") version libs.versions.loom.get()
}

base {
    archivesName = properties["archives_base_name"] as String
    version = libs.versions.mod.version.get()
    group = properties["maven_group"] as String
}

repositories {
    // Meteor repos (keep)
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }

    // NEW: needed for JDA
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.meteor.client)

    // --- DISCORD: JDA ---------------------------------------------------
    // Latest stable 6.x (you can bump this if needed)
    implementation("net.dv8tion:JDA:6.1.2")

    // --- BARITONE (compile-only) ----------------------------------------
    // Baritone doesn’t have an official maven for the Meteor fork.
    // Easiest approach:
    // 1. Download the same Baritone jar you use with Meteor from meteorclient.com
    // 2. Put it into:  /libs/baritone-1.21.10.jar
    // 3. Enable the line below (remove the leading //)
    //
//     modCompileOnly(files("libs/Baritone 1.21.4.jar"))
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to libs.versions.minecraft.get()
        )

        inputs.properties(propertyMap)

        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        inputs.property("archivesName", project.base.archivesName.get())

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}
