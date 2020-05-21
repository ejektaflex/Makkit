package io.ejekta.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.FillWallsOperation
import io.ejekta.makkit.common.enums.AirFillOption
import io.ejekta.makkit.common.enums.GuiCorner
import io.ejekta.makkit.common.enums.SideSelectionStyle
import io.ejekta.makkit.common.enums.UndoRedoMode
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW


class MakkitConfig {


    // General

    var legend = true

    var gridSnapping = true

    var historyHighlighting = true

    var sideSelectionStyle = SideSelectionStyle.SIMPLE

    // Operations

    var weightedPalette = false

    var randomRotate = false

    // Legend

    var showLegend = true

    var legendCorner = GuiCorner.BOTTOM_LEFT

    var showUtility = false

    var showBasic = true

    var showSystem = false

    //var showAdvanced = true


    // Visuals

    var axialTextSize = 1f

    var selectionBoxColor = RenderColor.GREEN

    var selectionFaceColor = RenderColor.YELLOW

    var multiplayerBoxColor = RenderColor.PINK


    // Keybinds

    var moveDragKey = Default.MOVE_DRAG
    var movePushKey = Default.MOVE_PUSH
    var fillKey = Default.FILL_AREA
    var wallsKey = Default.FILL_WALL
    var resizeSideKey = Default.RESIZE_SIDE
    var resizeSymmetricKey = Default.RESIDE_SIDE_SYMMETRIC
    var repeatPatternKey = Default.REPEAT_PATTERN
    var mirrorToolKey = Default.MIRROR_TOOL
    var copyKey = Default.COPY_KEY
    var pasteKey = Default.PASTE_KEY
    var newBoxKey = Default.NEW_BOX
    var undoKey = Default.UNDO
    var redoKey = Default.REDO
    var multiPalette = Default.MULTIPALETTE
    var airMode = Default.AIR_MODE
    var placeMode = Default.PLACE_MODE

    val keys: Set<KeyStateHandler>
        get() = setOf(
                moveDragKey,
                movePushKey,
                fillKey,
                wallsKey,
                resizeSideKey,
                resizeSymmetricKey,
                repeatPatternKey,
                mirrorToolKey,
                copyKey,
                pasteKey,
                newBoxKey,
                undoKey,
                redoKey,
                multiPalette,
                airMode,
                placeMode
        )


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(LiteralText("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(LiteralText("General"))

        val legend = builder.getOrCreateCategory(LiteralText("Legend"))

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

        // Legend

        legend.addEntry(
                entryBuilder.startBooleanToggle(
                        LiteralText("Show Legend"),
                        showLegend
                ).setDefaultValue(true).setTooltip(
                        LiteralText("Set to false if you don't want a keybinding legend to show up in"),
                        LiteralText("the corner when using Makkit")
                ).setSaveConsumer {
                    showLegend = it
                }.build()
        )

        legend.addEntry(
                entryBuilder.startEnumSelector(
                        LiteralText("Legend Corner"),
                        GuiCorner::class.java,
                        legendCorner
                ).setDefaultValue(GuiCorner.BOTTOM_LEFT).setTooltip(
                        LiteralText("Which corner the legend GUI should show up in")
                ).setSaveConsumer {
                    legendCorner = it
                }.build()
        )

        legend.addEntry(
                entryBuilder.startSubCategory(LiteralText("Categories to Show"),
                listOf(
                        entryBuilder.startBooleanToggle(
                                LiteralText("Utility Keys"),
                                showUtility
                        ).setDefaultValue(false).setTooltip(
                                LiteralText("Whether or not to show utility based keybinds in the legend"),
                                LiteralText("(move, resize, create new selection box)")
                        ).setSaveConsumer {
                            showUtility = it
                        }.build(),

                        entryBuilder.startBooleanToggle(
                                LiteralText("Basic Keys"),
                                showBasic
                        ).setDefaultValue(true).setTooltip(
                                LiteralText("Whether or not to show basic editor keybinds in the legend"),
                                LiteralText("(fill area / walls, repeat, mirror, palette tool)")
                        ).setSaveConsumer {
                            showBasic = it
                        }.build(),

                        entryBuilder.startBooleanToggle(
                                LiteralText("System Keys"),
                                showSystem
                        ).setDefaultValue(false).setTooltip(
                                LiteralText("Whether or not to show system-level editor keybinds"),
                                LiteralText("in the legend (copy/paste, undo/redo)")
                        ).setSaveConsumer {
                            showSystem = it
                        }.build()

                )).setExpanded(true).build()
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

        fun addKeybindEntry(default: KeyStateHandler, current: KeyStateHandler) {
            keybinds.addEntry(entryBuilder.startModifierKeyCodeField(
                    default.name, current.binding
            ).setDefaultValue(default.binding).setModifierSaveConsumer {
                current.binding = it
            }.build())
        }

        addKeybindEntry(Default.MOVE_DRAG, moveDragKey)
        addKeybindEntry(Default.MOVE_PUSH, movePushKey)
        addKeybindEntry(Default.RESIZE_SIDE, resizeSideKey)
        addKeybindEntry(Default.RESIDE_SIDE_SYMMETRIC, resizeSymmetricKey)
        addKeybindEntry(Default.MULTIPALETTE, multiPalette)
        addKeybindEntry(Default.NEW_BOX, newBoxKey)
        addKeybindEntry(Default.PLACE_MODE, placeMode)
        addKeybindEntry(Default.FILL_AREA, fillKey)
        addKeybindEntry(Default.FILL_WALL, wallsKey)
        addKeybindEntry(Default.REPEAT_PATTERN, repeatPatternKey)
        addKeybindEntry(Default.MIRROR_TOOL, mirrorToolKey)
        addKeybindEntry(Default.COPY_KEY, copyKey)
        addKeybindEntry(Default.PASTE_KEY, pasteKey)
        addKeybindEntry(Default.UNDO, undoKey)
        addKeybindEntry(Default.REDO, redoKey)


        return builder.build()
    }

    fun assignKeybinds() {
        fillKey.setKeyDown {
            MakkitClient.region?.doOperation(FillBlocksOperation())
        }

        wallsKey.setKeyDown {
            MakkitClient.region?.doOperation(FillWallsOperation())
        }

        undoKey.setKeyDown {
            EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
        }

        redoKey.setKeyDown {
            EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
        }

        newBoxKey.setKeyDown {
            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                val bhr = btr as BlockHitResult
                MakkitClient.getOrCreateRegion().centerOn(bhr.blockPos.offset(bhr.side))
            }
        }

        multiPalette.setKeyDown {
            val inv = MinecraftClient.getInstance().player?.inventory
            val slot = inv?.selectedSlot
            slot?.let { ClientPalette.addToPalette(it) }
        }

        placeMode.setKeyDown {
            MakkitClient.isInEditMode = !MakkitClient.isInEditMode
        }

        airMode.setKeyDown {
            val modeInd = MakkitClient.airModeOption.ordinal
            MakkitClient.airModeOption = enumValues<AirFillOption>()[(modeInd + 1) % enumValues<AirFillOption>().size]
        }
    }

    fun onSave() {
        save()
        MakkitClient.config = load()
    }

    companion object {

        private fun makkitKey(
                id: String,
                type: InputUtil.Type,
                code: Int,
                ctrl: Boolean = false,
                shift: Boolean = false,
                alt: Boolean = false
        ): KeyStateHandler {
            return KeyStateHandler(id,
                    ModifierKeyCode.of(
                            type.createFromCode(code),
                            Modifier.of(alt, ctrl, shift)
                    )
            )
        }

        // So many key binds!
        object Default {

            // Tool Keys
            val MOVE_DRAG: KeyStateHandler
                get() = makkitKey("move_dual_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT)
            val MOVE_PUSH: KeyStateHandler
                get() = makkitKey("move_single_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, alt = true)
            val FILL_AREA: KeyStateHandler
                get() = makkitKey("fill_blocks", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R)
            val FILL_WALL: KeyStateHandler
                get() = makkitKey("fill_walls", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C)
            val RESIZE_SIDE: KeyStateHandler
                get() = makkitKey("resize_single_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            val RESIDE_SIDE_SYMMETRIC: KeyStateHandler
                get() = makkitKey("resize_single_axis_symmetric", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, alt = true)
            val REPEAT_PATTERN: KeyStateHandler
                get() = makkitKey("repeat_pattern", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X)
            val MIRROR_TOOL: KeyStateHandler
                get() = makkitKey("mirror_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N)

            // Special Keys
            val MULTIPALETTE: KeyStateHandler
                get() = makkitKey("multi_palette", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V)
            val AIR_MODE: KeyStateHandler
                get() = makkitKey("air_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G)
            val PLACE_MODE: KeyStateHandler
                get() = makkitKey("place_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z)

            // Z Key should be Toggle Air Mode

            // Non-Tool Keys
            val COPY_KEY: KeyStateHandler
                get() = makkitKey("copy_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, true)
            val PASTE_KEY: KeyStateHandler
                get() = makkitKey("paste_tool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, true)
            val NEW_BOX: KeyStateHandler
                get() = makkitKey("center_edit_region", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B)
            val UNDO: KeyStateHandler
                get() = makkitKey("undo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, ctrl = true)
            val REDO: KeyStateHandler
                get() = makkitKey("redo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, ctrl = true)

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
                    val id = nextString()
                    val type = if (nextBoolean()) InputUtil.Type.KEYSYM else InputUtil.Type.MOUSE
                    val code = nextInt()
                    val alt = nextBoolean()
                    val ctrl = nextBoolean()
                    val shift = nextBoolean()
                    endArray()
                    return makkitKey(id, type, code,
                            ctrl = ctrl,
                            shift = shift,
                            alt = alt
                    )
                }
            }

        }

        val configPath = FabricLoader.getInstance().configDirectory.toPath().resolve(MakkitCommon.ID + ".json")


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
                // If no file exists, just silently save
                if (!configPath.toFile().exists()) {
                    save()
                } else {
                    println("Could not load MakkitConfig, using a default config..")
                    e.printStackTrace()
                    save()
                }

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