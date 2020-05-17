package io.ejekta.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.enum.SideSelectionStyle
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.MakkitCommon
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.loader.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText


class MakkitConfig {

    // General

    var gridSnapping = true

    var historyHighlighting = true

    var sideSelectionStyle = SideSelectionStyle.SIMPLE

    // Operations

    var weightedPalette = false

    var randomRotate = false

    // Visuals

    var axialTextSize = 1f

    var selectionBoxColor = RenderColor.GREEN

    var selectionFaceColor = RenderColor.YELLOW

    var multiplayerBoxColor = RenderColor.PINK

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(LiteralText("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(LiteralText("General"))

        val operations = builder.getOrCreateCategory(LiteralText("Operations"))

        val visuals = builder.getOrCreateCategory(LiteralText("Visuals"))

        val entryBuilder = builder.entryBuilder()

        // General

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

        general.addEntry(
                entryBuilder.startEnumSelector(
                        LiteralText("Side Selection Method"),
                        SideSelectionStyle::class.java,
                        sideSelectionStyle
                ).setDefaultValue(SideSelectionStyle.SIMPLE).setTooltip(
                        LiteralText("When set to SIMPLE, you must press a key to switch between"),
                        LiteralText("front and back face selection. When set to SMART, Makkit will"),
                        LiteralText("figure out which face you want to select based on which face"),
                        LiteralText("is closest to your cursor.")
                ).setSaveConsumer {
                    sideSelectionStyle = it
                }.build()
        )

        // Operations

        operations.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("Palette Weighting"),
                        weightedPalette
                ).setDefaultValue(false).setTooltip(
                        LiteralText("If true, fill operations will be weighted"),
                        LiteralText("based on the size of the stacks in your palette")
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

        // Visuals

        visuals.addEntry(
                entryBuilder.startFloatField(
                        LiteralText("3D Text Size Scaling"),
                        axialTextSize
                ).setDefaultValue(1f).setTooltip(
                        LiteralText("The size of text in the 3D world")
                ).setSaveConsumer {
                    axialTextSize = it
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        LiteralText("Selection Box Color"),
                        (selectionBoxColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.GREEN.intValue - 0xFF000000).toInt()).setTooltip(
                        LiteralText("The color of the selection box")
                ).setSaveConsumer {
                    selectionBoxColor = RenderColor(it)
                    MakkitClient.region?.selectionRenderer?.apply {
                        fillColor = selectionBoxColor.toAlpha(.4f)
                        edgeColor = selectionBoxColor.toAlpha(.4f)
                    }
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        LiteralText("Selection Face Color"),
                        (selectionFaceColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.YELLOW.intValue - 0xFF000000).toInt()).setTooltip(
                        LiteralText("The color of the selected face")
                ).setSaveConsumer {
                    selectionFaceColor = RenderColor(it)
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        LiteralText("Multiplayer Box Color"),
                        (multiplayerBoxColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.PINK.intValue - 0xFF000000).toInt()).setTooltip(
                        LiteralText("The color of the selection boxes of other players")
                ).setSaveConsumer {
                    multiplayerBoxColor = RenderColor(it)
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
                .registerTypeAdapter(RenderColor::class.java, RenderColor.typeAdapter)
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