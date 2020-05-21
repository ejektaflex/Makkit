package io.ejekta.makkit.client

import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditLegend
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.enums.AirFillOption
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack

class MakkitClient : ClientModInitializer {

    val mc = MinecraftClient.getInstance()

    override fun onInitializeClient() {

        // Clientbound
        FocusRegionPacket.registerS2C()
        ShadowBoxShowPacket.registerS2C()

        MakkitConfig.load()
        config.assignKeybinds()

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)
        Events.MouseScrollEvent.Dispatcher.register(::onScroll)
        Events.MouseClickedEvent.Dispatcher.register(::onGameClick)

        HudRenderCallback.EVENT.register(HudRenderCallback(::onHudRender))

    }

    fun onHudRender(matrixStack: MatrixStack, tickDelta: Float) {
        if (config.legend && mc.player?.isCreative == true && region?.isBeingInteractedWith() == true) {
            EditLegend.draw(matrixStack)
        }
    }



    private fun onScroll(e: Events.MouseScrollEvent) {
        val reg = getOrCreateRegion()
        reg.tryScrollFace(e.amount)

        if (config.multiPalette.isDown) {
            val holding = MinecraftClient.getInstance().player?.mainHandStack
            val slot = MinecraftClient.getInstance().player?.inventory?.selectedSlot

            if (holding != null && slot != null) {
                ClientPalette.addToPalette(slot)
            } else {
                ClientPalette.clearPalette()
            }

        } else {
            ClientPalette.clearPalette()
        }

    }

    // Return true if we want to cancel game interaction
    private fun onGameClick(e: Events.MouseClickedEvent): Boolean {
        region?.let {
            if (it.isBeingInteractedWith()) {
                return true
            }
        }
        return false
    }

    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

        // Maybe don't tie this to draw calls, but why fix what isn't broken?
        for (key in config.keys) {
            key.update()
        }


        RenderHelper.drawInWorld {
            region?.update()
            region?.draw()

            drawRemoteRegions()
        }
    }

    companion object {

        var isInEditMode = true

        var airModeOption = AirFillOption.ALL_BLOCKS

        var config = MakkitConfig.load()

        fun drawRemoteRegions() {
            for (entry in remoteBoxMap) {
                entry.value.draw(
                        colorFill = config.multiplayerBoxColor.toAlpha(.2f),
                        colorEdge = config.multiplayerBoxColor.toAlpha(.2f)
                )
            }
        }

        fun getOrCreateRegion(): EditRegion {
            if (region == null) {
                region = EditRegion()
            }
            return region!!
        }

        var remoteBoxMap = mutableMapOf<String, RenderBox>()

        var region: EditRegion? = null

    }

}