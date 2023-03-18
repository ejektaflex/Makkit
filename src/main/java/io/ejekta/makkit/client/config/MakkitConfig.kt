package io.ejekta.makkit.client.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditLegend
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.FillWallsOperation
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.enums.GuiCorner
import io.ejekta.makkit.common.enums.SideSelectionStyle
import io.ejekta.makkit.common.enums.UndoRedoMode
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
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

    var animations = true

    var animationSpeed = 25.0

    var selectionBoxColor = RenderColor.GREEN

    var selectionFaceColor = RenderColor.YELLOW

    var multiplayerBoxColor = RenderColor.PINK

    var pasteBoxColor = RenderColor.PURPLE


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
    var moveBoxKey = Default.MOVE_BOX
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
                moveBoxKey,
                undoKey,
                redoKey,
                multiPalette,
                airMode,
                placeMode
        )


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.literal("Makkit"))
                .setSavingRunnable(::onSave)

        val general = builder.getOrCreateCategory(Text.literal("General"))

        val legend = builder.getOrCreateCategory(Text.literal("Legend"))

        val operations = builder.getOrCreateCategory(Text.literal("Operations"))

        val visuals = builder.getOrCreateCategory(Text.literal("Visuals"))

        val animationsCat = builder.getOrCreateCategory(Text.literal("Animations"))

        val keybinds = builder.getOrCreateCategory(Text.literal("Keybinds"))

        val entryBuilder = builder.entryBuilder()

        // General

        general.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("Grid Snapping"),
                        gridSnapping
                ).setDefaultValue(true).setTooltip(
                        Text.literal("Whether or not the preview box will snap to the Minecraft block grid")
                ).setSaveConsumer {
                    gridSnapping = it
                }.build()
        )

        general.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("History Focus"),
                        historyHighlighting
                ).setDefaultValue(true).setTooltip(
                        Text.literal("Whether the selection box should change when hitting undo/redo")
                ).setSaveConsumer {
                    historyHighlighting = it
                }.build()
        )

        general.addEntry(
                entryBuilder.startEnumSelector(
                        Text.literal("Side Selection Method"),
                        SideSelectionStyle::class.java,
                        sideSelectionStyle
                ).setDefaultValue(SideSelectionStyle.SIMPLE).setTooltip(
                        Text.literal("When set to SIMPLE, you must press a key to switch between"),
                        Text.literal("front and back face selection. When set to SMART, Makkit will"),
                        Text.literal("figure out which face you want to select based on which face"),
                        Text.literal("is closest to your cursor. EXPERIMENTAL is a work in progress!")
                ).setSaveConsumer {
                    sideSelectionStyle = it
                }.build()
        )

        // Legend

        legend.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("Show Legend"),
                        showLegend
                ).setDefaultValue(true).setTooltip(
                        Text.literal("Set to false if you don't want a keybinding legend to show up in"),
                        Text.literal("the corner when using Makkit")
                ).setSaveConsumer {
                    showLegend = it
                }.build()
        )

        legend.addEntry(
                entryBuilder.startEnumSelector(
                        Text.literal("Legend Corner"),
                        GuiCorner::class.java,
                        legendCorner
                ).setDefaultValue(GuiCorner.BOTTOM_LEFT).setTooltip(
                        Text.literal("Which corner the legend GUI should show up in")
                ).setSaveConsumer {
                    legendCorner = it
                }.build()
        )

        legend.addEntry(
                entryBuilder.startSubCategory(Text.literal("Categories to Show"),
                listOf(
                        entryBuilder.startBooleanToggle(
                                Text.literal("Utility Keys"),
                                showUtility
                        ).setDefaultValue(false).setTooltip(
                                Text.literal("Whether or not to show utility based keybinds in the legend"),
                                Text.literal("(move, resize, create new selection box)")
                        ).setSaveConsumer {
                            showUtility = it
                        }.build(),

                        entryBuilder.startBooleanToggle(
                                Text.literal("Basic Keys"),
                                showBasic
                        ).setDefaultValue(true).setTooltip(
                                Text.literal("Whether or not to show basic editor keybinds in the legend"),
                                Text.literal("(fill area / walls, repeat, mirror, palette tool)")
                        ).setSaveConsumer {
                            showBasic = it
                        }.build(),

                        entryBuilder.startBooleanToggle(
                                Text.literal("System Keys"),
                                showSystem
                        ).setDefaultValue(false).setTooltip(
                                Text.literal("Whether or not to show system-level editor keybinds"),
                                Text.literal("in the legend (copy/paste, undo/redo)")
                        ).setSaveConsumer {
                            showSystem = it
                        }.build()

                )).setExpanded(true).build()
        )


        // Operations

        operations.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("Palette Weighting"),
                        weightedPalette
                ).setDefaultValue(false).setTooltip(
                        Text.literal("If true, fill operations will be weighted"),
                        Text.literal("based on the size of the stacks in your palette")
                ).setSaveConsumer {
                    weightedPalette = it
                }.build()
        )

        operations.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("Random Rotation"),
                        randomRotate
                ).setDefaultValue(false).setTooltip(
                        Text.literal("If true, fill operations will rotate blocks randomly, if possible")
                ).setSaveConsumer {
                    randomRotate = it
                }.build()
        )

        // Visuals

        visuals.addEntry(
                entryBuilder.startFloatField(
                        Text.literal("3D Text Size Scaling"),
                        axialTextSize
                ).setDefaultValue(1f).setTooltip(
                        Text.literal("The size of text in the 3D world")
                ).setSaveConsumer {
                    axialTextSize = it
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        Text.literal("Selection Box Color"),
                        (selectionBoxColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.GREEN.intValue - 0xFF000000).toInt()).setTooltip(
                        Text.literal("The color of the selection box")
                ).setSaveConsumer {
                    selectionBoxColor = RenderColor(it)
                    MakkitClient.region?.changeColors(selectionBoxColor.toAlpha(.4f))
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        Text.literal("Selection Face Color"),
                        (selectionFaceColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.YELLOW.intValue - 0xFF000000).toInt()).setTooltip(
                        Text.literal("The color of the selected face")
                ).setSaveConsumer {
                    selectionFaceColor = RenderColor(it)
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        Text.literal("Multiplayer Box Color"),
                        (multiplayerBoxColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.PINK.intValue - 0xFF000000).toInt()).setTooltip(
                        Text.literal("The color of the selection boxes of other players")
                ).setSaveConsumer {
                    multiplayerBoxColor = RenderColor(it)
                }.build()
        )

        visuals.addEntry(
                entryBuilder.startColorField(
                        Text.literal("Selection Face Color"),
                        (pasteBoxColor.intValue - 0xFF000000).toInt()
                ).setDefaultValue((RenderColor.PURPLE.intValue - 0xFF000000).toInt()).setTooltip(
                        Text.literal("The color of the selected face")
                ).setSaveConsumer {
                    pasteBoxColor = RenderColor(it)
                }.build()
        )

        // Animations

        animationsCat.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.literal("Animations"),
                        animations
                ).setDefaultValue(true).setTooltip(
                        Text.literal("Whether box resize animations should occur")
                ).setSaveConsumer {
                    animations = it
                }.build()
        )

        animationsCat.addEntry(
                entryBuilder.startDoubleField(
                        Text.literal("Animation Speed"),
                        animationSpeed
                ).setDefaultValue(25.0).setTooltip(
                        Text.literal("Box animation speed")
                ).setSaveConsumer {
                    animationSpeed = it
                }.setMin(5.0).setMax(100.0).build()
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
        addKeybindEntry(Default.MOVE_BOX, moveBoxKey)
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
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                MakkitClient.region?.doOperation(FillBlocksOperation())
            }
        }

        wallsKey.setKeyDown {
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                MakkitClient.region?.doOperation(FillWallsOperation())
            }
        }

        undoKey.setKeyDown {
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
            }

        }

        redoKey.setKeyDown {
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
            }
        }

        newBoxKey.setKeyDown {

            // Delete region if it exists and you are looking at it
            if (MakkitClient.region?.isBeingInteractedWith() == true) {
                MakkitClient.region = null
                ShadowBoxUpdatePacket(Box(BlockPos.ORIGIN), disconnect = true).sendToServer()
                return@setKeyDown
            }

            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                val bhr = btr as BlockHitResult
                MakkitClient.getOrCreateRegion().apply {
                    selection = Box(bhr.blockPos, bhr.blockPos.add(1.0, 1.0, 1.0))
                    selectionRenderer.directSet(Box(
                            bhr.pos, bhr.pos
                    ))
                    //selectionRenderer.shrinkToCenter()
                }
            }
        }

        moveBoxKey.setKeyDown {

            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                val bhr = btr as BlockHitResult
                MakkitClient.getOrCreateRegion().centerOn(bhr.blockPos)
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
            val modeInd = MakkitClient.blockMask.ordinal
            MakkitClient.blockMask = enumValues<BlockMask>()[(modeInd + 1) % enumValues<BlockMask>().size]
        }
    }

    fun onSave() {
        save()
        MakkitClient.config = load()
        EditLegend.populateLegend()
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

        // So many keybinds!
        object Default {

            // Tool Keys
            val MOVE_DRAG: KeyStateHandler
                get() = makkitKey("move_dual_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            val MOVE_PUSH: KeyStateHandler
                get() = makkitKey("move_single_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, alt = true)
            val FILL_AREA: KeyStateHandler
                get() = makkitKey("fill_blocks", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R)
            val FILL_WALL: KeyStateHandler
                get() = makkitKey("fill_walls", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C)
            val RESIZE_SIDE: KeyStateHandler
                get() = makkitKey("resize_single_axis", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT)
            val RESIDE_SIDE_SYMMETRIC: KeyStateHandler
                get() = makkitKey("resize_single_axis_symmetric", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, alt = true)
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
            val MOVE_BOX: KeyStateHandler
                get() = makkitKey("move_edit_region", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, alt = true)
            val UNDO: KeyStateHandler
                get() = makkitKey("undo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, ctrl = true)
            val REDO: KeyStateHandler
                get() = makkitKey("redo", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, ctrl = true)

        }

        class KeyAdapter : TypeAdapter<KeyStateHandler>() {
            override fun write(out: JsonWriter, value: KeyStateHandler) {
                out.apply {
                    beginArray()
                    value(value.id)
                    value(value.binding.type == InputUtil.Type.KEYSYM)
                    value(value.binding.keyCode.code)
                    value(value.binding.modifier.hasAlt())
                    value(value.binding.modifier.hasControl())
                    value(value.binding.modifier.hasShift())
                    endArray()
                }
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

        val configPath = FabricLoader.getInstance().configDir.resolve(MakkitCommon.ID + ".json")

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