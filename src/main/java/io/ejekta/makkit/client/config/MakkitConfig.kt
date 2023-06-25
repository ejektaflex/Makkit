package io.ejekta.makkit.client.config

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.gui.ToolScreen
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
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import org.lwjgl.glfw.GLFW


open class MakkitConfig {


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
    var fillKey = Default.ACTION_A
    var wallsKey = Default.ACTION_B
    var resizeSideKey = Default.RESIZE_SIDE

    val keys: Set<KambrikKeybind>
        get() = setOf(
                moveDragKey,
                movePushKey,
                fillKey,
                wallsKey,
                resizeSideKey
        )


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
                //.setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.literal("Makkit"))
                //.setSavingRunnable(::onSave)

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

        fun addKeybindEntry(default: KambrikKeybind, current: KambrikKeybind) {
            keybinds.addEntry(entryBuilder.startModifierKeyCodeField(
                    Text.translatable("${MakkitCommon.ID}.${default.translation}"), ModifierKeyCode.of(default.defaultKey, Modifier.none())
            ).setDefaultValue(default.defaultKey).setModifierSaveConsumer {
                //current.binding = it
                // TODO binding config!
            }.build())
        }

        addKeybindEntry(Default.MOVE_DRAG, moveDragKey)
        addKeybindEntry(Default.MOVE_PUSH, movePushKey)
        //addKeybindEntry(Default.MULTIPALETTE, multiPalette)


        return builder.build()
    }

    fun assignKeybinds() {



        fillKey.onDown {
            println("Fill key pressed")
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                println("Trying to do fill blocks op")
                MakkitClient.region?.doOperation(FillBlocksOperation())
            }
        }

        wallsKey.onDown {
            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
                MakkitClient.region?.doOperation(FillWallsOperation())
            }
        }

//        undoKey.onDown {
//            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
//                EditHistoryPacket(UndoRedoMode.UNDO).sendToServer()
//            }
//
//        }
//
//        redoKey.onDown {
//            if (MakkitClient.region?.isAnyToolBeingUsed() == false) {
//                EditHistoryPacket(UndoRedoMode.REDO).sendToServer()
//            }
//        }

//        newBoxKey.onDown {
//
//            // Delete region if it exists and you are looking at it
//            if (MakkitClient.region?.isBeingInteractedWith() == true) {
//                MakkitClient.region = null
//                ShadowBoxUpdatePacket(Box(BlockPos.ORIGIN), disconnect = true).sendToServer()
//                return@onDown
//            }
//
//            val btr = MinecraftClient.getInstance().crosshairTarget
//            if (btr != null && btr.type == HitResult.Type.BLOCK) {
//                val bhr = btr as BlockHitResult
//                MakkitClient.getOrCreateRegion().apply {
//                    selection = Box(bhr.blockPos, bhr.blockPos.add(1.0, 1.0, 1.0))
//                    selectionRenderer.directSet(Box(
//                            bhr.pos, bhr.pos
//                    ))
//                    //selectionRenderer.shrinkToCenter()
//                }
//            }
//        }
//
//        moveBoxKey.onDown {
//
//            val btr = MinecraftClient.getInstance().crosshairTarget
//            if (btr != null && btr.type == HitResult.Type.BLOCK) {
//                val bhr = btr as BlockHitResult
//                MakkitClient.getOrCreateRegion().centerOn(bhr.blockPos)
//            }
//
//        }
//
//        multiPalette.onDown {
//            val inv = MinecraftClient.getInstance().player?.inventory
//            val slot = inv?.selectedSlot
//            slot?.let { ClientPalette.addToPalette(it) }
//        }
//
//        placeMode.onDown {
//            MakkitClient.isInEditMode = !MakkitClient.isInEditMode
//        }
//
//        airMode.onDown {
//            val modeInd = MakkitClient.blockMask.ordinal
//            MakkitClient.blockMask = enumValues<BlockMask>()[(modeInd + 1) % enumValues<BlockMask>().size]
//        }
    }

    companion object {

        private fun makkitKey(
                id: String,
                code: Int,
                ctrl: Boolean = false,
                shift: Boolean = false,
                alt: Boolean = false
        ): KambrikKeybind {
            return Kambrik.Input.registerKeyboardBinding(
                code, id, "doot", realTime = true, register = false
            ) {

            }.also {
                println("Registered makkit keybind! ID: $id")
            }
        }

        private fun makkitMouse(
            id: String,
            code: Int,
            ctrl: Boolean = false,
            shift: Boolean = false,
            alt: Boolean = false
        ): KambrikKeybind {
            return Kambrik.Input.registerMouseBinding(
                code, id, "doot", realTime = true, register = false
            ) {

            }.also {
                println("Registered makkit keybind! ID: $id")
            }
        }

        // So many keybinds!
        object Default {


            val NEW_TEST = Kambrik.Input.registerKeyboardBinding(
                GLFW.GLFW_KEY_R, "do_something_new", "doot", realTime = true
            ) {
                onDown {
                    println("New test key pressed!")
                    MinecraftClient.getInstance().setScreen(
                        ToolScreen()
                    )
                }
            }.also {
                println("Registered makkit keybind! ID: ${it.modKey}")
            }

            // Tool Keys
            val MOVE_DRAG: KambrikKeybind
                get() = makkitMouse("move_dual_axis", GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            val MOVE_PUSH: KambrikKeybind
                get() = makkitMouse("move_single_axis", GLFW.GLFW_MOUSE_BUTTON_RIGHT, alt = true)

            val POWER: KambrikKeybind
                get() = makkitKey("power", GLFW.GLFW_KEY_Z)

            val ACTION_A: KambrikKeybind
                get() = makkitKey("fill_blocks", GLFW.GLFW_KEY_X)

            val ACTION_B: KambrikKeybind
                get() = makkitKey("fill_walls", GLFW.GLFW_KEY_C)

            val RESIZE_SIDE: KambrikKeybind
                get() = makkitMouse("resize_single_axis", GLFW.GLFW_MOUSE_BUTTON_LEFT)

            val RESIDE_SIDE_SYMMETRIC: KambrikKeybind
                get() = makkitMouse("resize_single_axis_symmetric", GLFW.GLFW_MOUSE_BUTTON_LEFT, alt = true)

        }



        val configPath = FabricLoader.getInstance().configDir.resolve(MakkitCommon.ID + ".json")





        fun load(): MakkitConfig {

            return try {
                MakkitConfig()
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
//            configPath.toFile().writeText(
//                    GSON.toJson(MakkitClient.config, MakkitConfig::class.java)
//            )
            //MakkitClient.config = load()
        }
    }

}