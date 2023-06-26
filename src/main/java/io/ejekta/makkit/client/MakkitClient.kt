package io.ejekta.makkit.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.client.render.AnimBox
import io.ejekta.makkit.client.render.MyLayers
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.ext.minus
import io.ejekta.makkit.common.ext.plus
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class MakkitClient : ClientModInitializer {

    override fun onInitializeClient() {

        Kambrik.Message.registerClientMessage(
            FocusRegionPacket.serializer(),
            FocusRegionPacket::class,
            Identifier(MakkitCommon.ID, "focus_region")
        )

        Kambrik.Message.registerClientMessage(
            ShadowBoxShowPacket.serializer(),
            ShadowBoxShowPacket::class,
            Identifier(MakkitCommon.ID, "shadow_box_show")
        )

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)
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

        fun onHudRender(context: DrawContext, tickDelta: Float) {
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
//            region?.let {
//                if (isInEditMode && it.isBeingInteractedWith()) {
//                    return true
//                }
//            }
            return false
        }

        var time: Long = System.currentTimeMillis()

        private fun onDrawScreen(e: Events.DrawScreenEvent) {
            // RenderHelper state
            RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

            if (mc.player?.isCreative == false) {
                return
            }

            RenderHelper.drawInWorld {
                val newTime = System.currentTimeMillis()
                val delta = newTime - time
                region?.update(delta)
                region?.draw()
                time = newTime
                handleRemoteRegions(delta)

                drawBlockFaces(
                    BlockPos(0, 64, 0)
                )

                mc.player?.let {
                    //println("DRAW FACE")
                    drawBlockFaces(
                        it.blockPos.north(2),
                        RenderColor.BLUE,
                        MyLayers.OVERLAY_QUADS
                    )

                    drawLine(
                        it.getCameraPosVec(tickDelta) - Vec3d(0.1, 0.1, 0.1), it.eyePos + Vec3d(4.0, 5.0, 6.0)
                    )

                    drawBoxEdges(
                        Box(it.blockPos, it.blockPos + BlockPos(1, 2, 3))
                    )
                }

            }
        }

        var isInEditMode = true

        var blockMask = BlockMask.ALL_BLOCKS

        private fun handleRemoteRegions(delta: Long) {
            for (entry in remoteBoxMap) {
                entry.value.update(delta)
                entry.value.render.draw(
                    colorFill = multiplayerBoxColor.toAlpha(.2f),
                    colorEdge = multiplayerBoxColor.toAlpha(.2f)
                )
            }
        }

        fun getOrCreateRegion(): EditRegion {
            if (region == null) {
                region = EditRegion(drawDragPlane = true)
            }
            return region!!
        }

        var remoteBoxMap = mutableMapOf<String, AnimBox>()

        var region: EditRegion? = null
    }

}