package io.ejekta.makkit.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditLegend
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.client.render.AnimBox
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

class MakkitClient : ClientModInitializer {

    override fun onInitializeClient() {

        Kambrik.Message.registerClientMessage(
            FocusRegionPacket.serializer(),
            Identifier(MakkitCommon.ID, "focus_region")
        )

        Kambrik.Message.registerClientMessage(
            ShadowBoxShowPacket.serializer(),
            Identifier(MakkitCommon.ID, "shadow_box_show")
        )
        config.assignKeybinds()

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)
        Events.InventoryScrolledEvent.Dispatcher.register(::onInvScroll)
        Events.MouseClickedEvent.Dispatcher.register(::onGameClick)

        HudRenderCallback.EVENT.register(HudRenderCallback(::onHudRender))
        UseBlockCallback.EVENT.register(onUseBlock())
    }

    companion object {

        private val mc = MinecraftClient.getInstance()

        fun onHudRender(matrixStack: MatrixStack, tickDelta: Float) {
            if (config.legend && mc.player?.isCreative == true && region?.isBeingInteractedWith() == true) {
                EditLegend.draw(matrixStack)
            }
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

        private fun updateKeys() {

//            for (key in config.keys) {
//                if (MinecraftClient.getInstance().currentScreen == null && !key.isDown)
//            }

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
            region?.let {
                if (isInEditMode && it.isBeingInteractedWith()) {
                    return true
                }
            }
            return false
        }

        var time: Long = System.currentTimeMillis()

        private fun onDrawScreen(e: Events.DrawScreenEvent) {
            // RenderHelper state
            RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

            if (mc.player?.isCreative == false) {
                return
            }

            updateKeys()

            RenderHelper.drawInWorld {
                val newTime = System.currentTimeMillis()
                val delta = newTime - time
                region?.update(delta)
                region?.draw()
                time = newTime
                handleRemoteRegions(delta)
            }
        }

        var isInEditMode = true

        var blockMask = BlockMask.ALL_BLOCKS

        var config = MakkitConfig()

        private fun handleRemoteRegions(delta: Long) {
            for (entry in remoteBoxMap) {
                entry.value.update(delta)
                entry.value.render.draw(
                    colorFill = config.multiplayerBoxColor.toAlpha(.2f),
                    colorEdge = config.multiplayerBoxColor.toAlpha(.2f)
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