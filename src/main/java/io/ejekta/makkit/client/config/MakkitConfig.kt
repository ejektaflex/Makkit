package io.ejekta.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.editor.input.MakkitKeys
import io.ejekta.makkit.common.enums.SideSelectionStyle
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.FillWallsOperation
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.fabricmc.loader.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW


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


    // Keybinds

    var moveDragKey = Default.MOVE_DRAG
    var fillKey = Default.FILL_AREA
    var wallsKey = Default.FILL_WALL
    var resizeSideKey = Default.RESIZE_SIDE
    var resizeSymmetricBinding = Default.RESIDE_SIDE_SYMMETRIC
    var repeatPatternBinding = Default.REPEAT_PATTERN
    var mirrorToolBinding = Default.MIRROR_TOOL

    val keys: Set<KeyStateHandler>
        get() = setOf(
                moveDragKey,
                fillKey,
                wallsKey,
                resizeSideKey,
                resizeSymmetricBinding,
                repeatPatternBinding,
                mirrorToolBinding
        )


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(LiteralText("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(LiteralText("General"))

        val operations = builder.getOrCreateCategory(LiteralText("Operations"))

        val visuals = builder.getOrCreateCategory(LiteralText("Visuals"))

        val keybinds = builder.getOrCreateCategory(LiteralText("Keybinds"))

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

        // Keybinds

        fun addKeybindEntry(name: String, default: KeyStateHandler, current: KeyStateHandler) {
            keybinds.addEntry(entryBuilder.startModifierKeyCodeField(
                    LiteralText(name), current.binding
            ).setDefaultValue(default.binding).setModifierSaveConsumer {
                current.binding = it
            }.build())
        }

        addKeybindEntry("Move Tool", Default.MOVE_DRAG, moveDragKey)
        addKeybindEntry("Fill Area Tool", Default.FILL_AREA, fillKey)
        addKeybindEntry("Fill Walls Tool", Default.FILL_WALL, wallsKey)
        addKeybindEntry("Reside Side Tool", Default.RESIZE_SIDE, resizeSideKey)
        addKeybindEntry("Reside Side (Symmetric)", Default.RESIDE_SIDE_SYMMETRIC, resizeSymmetricBinding)
        addKeybindEntry("Repeat Pattern Tool", Default.REPEAT_PATTERN, repeatPatternBinding)
        addKeybindEntry("Mirror Tool", Default.MIRROR_TOOL, mirrorToolBinding)


        return builder.build()
    }

    fun assignKeybinds() {
        fillKey.setKeyDown {
            MakkitClient.region?.doOperation(FillBlocksOperation())
        }

        wallsKey.setKeyDown {
            MakkitClient.region?.doOperation(FillWallsOperation())
        }
    }

    fun onSave() {
        save()
        MakkitClient.config = load()
    }

    companion object {

        private fun makkitKey(path: String, type: InputUtil.Type, code: Int, ctrl: Boolean = false): KeyStateHandler {
            println("Creating key $path")
            return KeyStateHandler(path,
                    ModifierKeyCode.of(
                            type.createFromCode(code),
                            Modifier.of(false, ctrl, false)
                    )
            )
        }

        object Default {
            val MOVE_DRAG: KeyStateHandler
                get() = makkitKey("move_dual_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z)
            val FILL_AREA: KeyStateHandler
                get() = makkitKey("fill_blocks", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R)
            val FILL_WALL: KeyStateHandler
                get() = makkitKey("fill_walls", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V)
            val RESIZE_SIDE: KeyStateHandler
                get() = makkitKey("resize_single_axis", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C)
            val RESIDE_SIDE_SYMMETRIC: KeyStateHandler
                get() = makkitKey("resize_single_axis_symmetric", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X)
            val REPEAT_PATTERN: KeyStateHandler
                get() = makkitKey("repeat_pattern", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G)
            val MIRROR_TOOL: KeyStateHandler
                get() = makkitKey("mirror_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N)


            val CHANGE_ME: KeyStateHandler = makkitKey("change_me", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L)

        }

        class KeyAdapter : TypeAdapter<KeyStateHandler>() {
            override fun write(out: JsonWriter, value: KeyStateHandler) {
                out.beginArray()
                out.value(value.id)
                val bool = value.binding.type == InputUtil.Type.KEYSYM
                out.value(bool)
                out.value(value.binding.keyCode.keyCode)
                out.value(value.binding.modifier.hasAlt())
                out.value(value.binding.modifier.hasControl())
                out.value(value.binding.modifier.hasShift())
                out.endArray()
            }

            override fun read(`in`: JsonReader): KeyStateHandler {
                `in`.apply {
                    beginArray()
                    var id = nextString()
                    var type = if (nextBoolean()) InputUtil.Type.KEYSYM else InputUtil.Type.MOUSE
                    var code = nextInt()
                    var alt = nextBoolean()
                    var ctrl = nextBoolean()
                    var shift = nextBoolean()
                    endArray()
                    return makkitKey(id, type, code, ctrl = ctrl)
                }
            }

        }

        val configPath = FabricLoader.INSTANCE.configDirectory.toPath().resolve(MakkitCommon.ID + ".json")


        private val GSON = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .registerTypeAdapter(RenderColor::class.java, RenderColor.typeAdapter)
                .registerTypeAdapter(KeyStateHandler::class.java, KeyAdapter())
                .create()



        fun load(): MakkitConfig {

            return try {
                GSON.fromJson(
                        configPath.toFile().readText(),
                        MakkitConfig::class.java
                ).also {
                    it.assignKeybinds()
                }
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