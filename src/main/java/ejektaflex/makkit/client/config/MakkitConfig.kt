package ejektaflex.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.TypeAdapter
import com.google.gson.annotations.Expose
import com.google.gson.stream.JsonReader
import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.common.MakkitCommon
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.fabricmc.loader.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW
import java.lang.reflect.Type
import java.nio.file.Files


class MakkitConfig() {

    var gridSnapping = false

    var historyHighlighting = false

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(LiteralText("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(LiteralText("Makkit"))

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

}