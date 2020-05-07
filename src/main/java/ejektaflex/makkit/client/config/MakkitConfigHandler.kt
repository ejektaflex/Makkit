package ejektaflex.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import ejektaflex.makkit.client.MakkitClient
import net.fabricmc.loader.FabricLoader
import java.nio.file.Files

object MakkitConfigHandler {

    val configPath = FabricLoader.INSTANCE.configDirectory.toPath().resolve(MakkitClient.ID + ".json")

    private val GSON = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create()

    fun load(): MakkitConfig {

        if (!Files.exists(configPath)) {
            println("Oh no")
            save()
        }

        return try {
            GSON.fromJson(
                    configPath.toFile().readText(),
                    MakkitConfig::class.java
            )
        } catch (e: Exception) {
            println("Could not load MakkitConfig, using a default config..")
            e.printStackTrace()
            MakkitConfig()
        }

    }

    fun save() {
        configPath.toFile().writeText(
                GSON.toJson(MakkitClient.config, MakkitConfig::class.java)
        )
        MakkitClient.config = load()
    }

}