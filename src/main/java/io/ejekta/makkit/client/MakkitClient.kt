package io.ejekta.makkit.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.input.KambrikKeyModifier
import io.ejekta.kambrik.input.KambrikModifiedBind
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.tools.MakkitTool
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.client.render.AnimBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.FillWallsOperation
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.ext.draw
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.InputUtil
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW

class MakkitClient : ClientModInitializer {

    override fun onInitializeClient() {

        Events.RenderWorldEvent.Dispatcher.register(::onDrawScreen)
        Events.InventoryScrolledEvent.Dispatcher.register(::onInvScroll)
        Events.MouseClickedEvent.Dispatcher.register(::onGameClick)

        HudRenderCallback.EVENT.register(HudRenderCallback(::onHudRender))
        UseBlockCallback.EVENT.register(onUseBlock())

    }

    companion object {

        var selectionBoxColor = RenderColor.GREEN

        var selectionFaceColor = RenderColor.YELLOW

        var multiplayerBoxColor = RenderColor.PINK

        var pasteBoxColor = RenderColor.PURPLE

        private val mc = MinecraftClient.getInstance()

        var randomRotate = true
        var weightedPalette = false
        var gridSnapping = true
        var animations = true
        var animationSpeed = 20.0 // 5 to 100
        var axialTextSize = 1f
        var historyHighlighting = true

        fun onHudRender(context: DrawContext, tickCounter: RenderTickCounter) {
//            if (config.legend && mc.player?.isCreative == true && region?.isBeingInteractedWith() == true) {
//                //EditLegend.draw(context)
//            }
        }

        // Use client's Block Palette for block placement, if it's active
        private fun onUseBlock() = UseBlockCallback { player, world, hand, hitResult ->
            if (world is ClientWorld && ClientPalette.hasAnyItems()) {
                ClientPalette.getRandomBlockSlot()?.let {
                    player?.inventory?.selectedSlot = it
                }
            }
            ActionResult.PASS
        }

        private fun onInvScroll(e: Events.InventoryScrolledEvent) {
//            if (config.multiPalette.isDown) {
//                val holding = MinecraftClient.getInstance().player?.mainHandStack
//
//                if (holding != null) {
//                    ClientPalette.addToPalette(e.newSlot)
//                } else {
//                    ClientPalette.clearPalette()
//                }
//
//            } else {
//                ClientPalette.clearPalette()
//            }
        }

        // Return true if we want to cancel game interaction
        private fun onGameClick(e: Events.MouseClickedEvent): Boolean {
            // TODO holding right click on a region and dragging mouse off the region while holding a block causes block placement spam
            region?.let {
                if (isInEditMode && it.isBeingInteractedWith() && mc.currentScreen == null) {
                    return true
                }
            }
            return false
        }

        var time: Long = System.currentTimeMillis()
        var timeDelta: Long = 0

        private fun onDrawScreen(e: Events.RenderWorldEvent) {
            // RenderHelper state
            RenderHelper.setState(e.matrices, e.tickCounter.getTickDelta(true), e.camera, e.buffers, e.matrix)

            if (mc.player?.isCreative == false) {
                return
            }


            RenderHelper.drawInWorld {
                val newTime = System.currentTimeMillis()
                timeDelta = newTime - time
                region?.update(timeDelta)

                region?.draw()

                time = newTime
                handleRemoteRegions(timeDelta)

                drawPoint(Vec3d(68.0, -58.0, 68.0), size = 0.1)

                drawText(Vec3d(68.0, -58.0, 68.0), "Hello!", textSize = 5.0f)

                drawBoxFilled(
                    Box.enclosing(
                        BlockPos(60, -58, 60),
                        BlockPos(62, -58, 62)
                    ),
                    RenderColor.PINK.toAlpha(0.8f)
                )

                drawBoxFilled(
                    Box.enclosing(
                        BlockPos(60, -58, 60),
                        BlockPos(62, -58, 62)
                    ),
                    RenderColor.PINK.toAlpha(0.8f)
                )

                drawBoxEdges(
                    Box.enclosing(
                        BlockPos(60, -58, 60),
                        BlockPos(62, -58, 62)
                    )
                )

            }
        }

        var isInEditMode = true

        var blockMask = BlockMask.ALL_BLOCKS

        private fun handleRemoteRegions(delta: Long) {
            for (entry in remoteBoxMap) {
                entry.value.update(delta)
                entry.value.renderBox.draw(
                    colorFill = multiplayerBoxColor.toAlpha(.2f),
                    colorEdge = multiplayerBoxColor.toAlpha(.2f)
                )
            }
        }

        fun getOrCreateRegion(): EditRegion {
            if (region == null) {
                region = EditRegion(drawDragPlane = false)
            }
            return region!!
        }

        var remoteBoxMap = mutableMapOf<String, AnimBox>()

        var region: EditRegion? = null
    }

    val powerKey = Kambrik.Input.registerBinding(
        KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, -1)
        ), realTime = true
    ) {
        onDown {
            println("POWER KEY!")

            // Delete region if it exists, and you are looking at it
            if (region?.isBeingInteractedWith() == true) {
                region = null
                //ShadowBoxUpdatePacket(Box(BlockPos.ORIGIN), disconnect = true).sendToServer()
                return@onDown
            }

            val btr = MinecraftClient.getInstance().crosshairTarget
            if (btr != null && btr.type == HitResult.Type.BLOCK) {
                val bhr = btr as BlockHitResult
                getOrCreateRegion().apply {
                    selection = Box.enclosing(bhr.blockPos, bhr.blockPos)
                    selectionRenderer.setImmediate(Box(
                        bhr.pos, bhr.pos
                    ))
                }
            }
        }
    }

    val fillKey = Kambrik.Input.registerBinding(
        KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_R, -1)
        ), realTime = true
    ) {
        onDown {
            val reg = getOrCreateRegion()
            reg.doOperation(
                FillBlocksOperation
            )
        }
    }

    val wallKey = Kambrik.Input.registerBinding(
        KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, -1)
        ), realTime = true
    ) {
        onDown {
            val reg = getOrCreateRegion()
            reg.doOperation(
                FillWallsOperation
            )
        }
    }

    fun regBind(bindModifiedBind: KambrikModifiedBind, toolEnum: MakkitTool) {
        Kambrik.Input.registerBinding(
            bindModifiedBind, realTime = true
        ) {
            onDown { region?.startUsingTool(toolEnum) }
            onUp { region?.stopUsingTool() }
        }
    }

    init {
        regBind(KambrikModifiedBind.Mouse(
            GLFW.GLFW_MOUSE_BUTTON_LEFT
        ), MakkitTool.RESIZE_AXIAL)

        regBind(KambrikModifiedBind.Mouse(
            GLFW.GLFW_MOUSE_BUTTON_RIGHT
        ), MakkitTool.MOVE_PLANAR)

        regBind(KambrikModifiedBind.Mouse(
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            KambrikKeyModifier(alt = true)
        ), MakkitTool.RESIZE_SYMMETRIC)

        regBind(KambrikModifiedBind.Mouse(
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            KambrikKeyModifier(alt = true)
        ), MakkitTool.MOVE_AXIAL)

//        regBind(KambrikModifiedBind.Key(
//            InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, -1)
//        ), MakkitTool.RESIZE_SYMMETRIC)

        regBind(KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_N, -1)
        ), MakkitTool.MIRROR)

        regBind(KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_X, -1)
        ), MakkitTool.PATTERN)

        regBind(KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, -1),
            KambrikKeyModifier(ctrl = true)
        ), MakkitTool.COPY)

        regBind(KambrikModifiedBind.Key(
            InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, -1),
            KambrikKeyModifier(ctrl = true)
        ), MakkitTool.PASTE)

    }

}
