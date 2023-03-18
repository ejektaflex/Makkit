import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.10"
	kotlin("plugin.serialization") version "1.6.0"
	id("fabric-loom") version "1.1-SNAPSHOT"
	`idea`
}

loom {
	accessWidenerPath.set(file("src/main/resources/makkit.accesswidener"))
}

object Versions {
	const val Minecraft = "1.19.2"
	object Jvm {
		val Java = JavaVersion.VERSION_17
		const val Kotlin = "1.8.10"
		const val TargetKotlin = "17"
	}
	object Fabric {
		const val Yarn = "1.19.2+build.28"
		const val Loader = "0.14.17"
		const val Api = "0.76.0+1.19.2"
	}
	object Mod {
		const val Group = "io.ejekta"
		const val ID = "makkit"
		const val Version = "4.0.0-SNAPSHOT"
	}
	object Env {
		const val Kambrik = "5.0.0-1.19.2-SNAPSHOT+"
		const val FLK = "1.9.1+kotlin.1.8.10"
		const val ClothConfig = "8.2.88"
		const val ModMenu = "4.2.0-beta.2"
	}
}

java {
	sourceCompatibility = Versions.Jvm.Java
	targetCompatibility = Versions.Jvm.Java
	withSourcesJar()
	withJavadocJar()
}

project.group = Versions.Mod.Group
version = Versions.Mod.Version



//compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
	mavenLocal()
	mavenCentral()
	//maven(url = "https://kotlin.bintray.com/kotlinx")
	maven(url = "https://maven.shedaniel.me/")
	maven(url = "https://maven.terraformersmc.com/") {
		name = "Mod Menu"
	}
}

dependencies {
	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${Versions.Minecraft}")
	mappings("net.fabricmc:yarn:${Versions.Fabric.Yarn}:v2")
	modImplementation("net.fabricmc:fabric-loader:${Versions.Fabric.Loader}")

	// Kambrik API
	modImplementation("io.ejekta:kambrik:${Versions.Env.Kambrik}")

	modApi("me.shedaniel.cloth:cloth-config-fabric:${Versions.Env.ClothConfig}") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	implementation("com.google.code.findbugs:jsr305:3.0.2")

	modApi("com.terraformersmc:modmenu:${Versions.Env.ModMenu}") {
		exclude(module = "fabric-api")
		exclude(module = "config-2")
	}

	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = Versions.Env.FLK)

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.Fabric.Api}")
}


tasks.getByName<ProcessResources>("processResources") {
	filesMatching("fabric.mod.json") {
		expand(
			mutableMapOf<String, String>(
				"modid" to Versions.Mod.ID,
				"version" to Versions.Mod.Version,
				"kotlinVersion" to Versions.Jvm.Kotlin,
				"fabricApiVersion" to Versions.Fabric.Api
			)
		)
	}
}

configurations.all {
	resolutionStrategy {
		force("net.fabricmc:fabric-loader:${Versions.Fabric.Loader}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = Versions.Jvm.TargetKotlin
	}
}
