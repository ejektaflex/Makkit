package io.ejekta.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.MakkitCommon
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.loader.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText
import java.nio.file.Files


class MakkitConfig() {

    var gridSnapping = false

    var historyHighlighting = false

    var weightedPalette = false

    var randomRotate = false

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(LiteralText("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(LiteralText("General"))

        val operations = builder.getOrCreateCategory(LiteralText("Operations"))

        val entryBuilder = builder.entryBuilder()

        general.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("Grid Snapping"),
                        gridSnapping
                ).setDefaultValue(true).setTooltip(
                        LiteralText("Whether or not the preview box will snap to the Minecraft block grid")
                ).setSaveConsumer {
                    gridSnapping = it
                }.build()
        )

        general.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("History Focus"),
                        historyHighlighting
                ).setDefaultValue(true).setTooltip(
                        LiteralText("Whether the selection box should change when hitting undo/redo")
                ).setSaveConsumer {
                    historyHighlighting = it
                }.build()
        )



        operations.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("Palette Weighting"),
                        weightedPalette
                ).setDefaultValue(false).setTooltip(
                        LiteralText("If true, fill operations will be weighted based on the size of the stacks in your palette")
                ).setSaveConsumer {
                    weightedPalette = it
                }.build()
        )

        operations.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("Random Rotation"),
                        randomRotate
                ).setDefaultValue(false).setTooltip(
                        LiteralText("If true, fill operations will rotate blocks randomly, if possible")
                ).setSaveConsumer {
                    randomRotate = it
                }.build()
        )

        return builder.build()
    }

    fun onSave() {
        save()
    }

    companion object {
        val configPath = FabricLoader.INSTANCE.configDirectory.toPath().resolve(MakkitCommon.ID + ".json")

        private val GSON = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create()

        fun load(): MakkitConfig {

            /*
            if (!Files.exists(configPath)) {
                println("Oh no")
                save()
            }

             */

            return try {
                GSON.fromJson(
                        configPath.toFile().readText(),
                        MakkitConfig::class.java
                )
            } catch (e: Exception) {
                println("Could not load MakkitConfig, using a default config..")
                e.printStackTrace()
                save()
                MakkitConfig()
            }

        }

        fun save() {
            configPath.toFile().writeText(
                    GSON.toJson(MakkitClient.config, MakkitConfig::class.java)
            )
            //MakkitClient.config = load()
        }
    }

}