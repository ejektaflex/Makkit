package io.ejekta.makkit.client

import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.InputState
import io.ejekta.makkit.client.editor.input.ClientPalette
import io.ejekta.makkit.client.editor.input.MakkitKeys
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.client.keys.KeyRemapper
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.client.render.RenderColor
import io.ejekta.makkit.client.render.RenderHelper
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class MakkitClient : ClientModInitializer {

    override fun onInitializeClient() {

        // Clientbound
        FocusRegionPacket.registerS2C()
        ShadowBoxShowPacket.registerS2C()

        MakkitConfig.load()

        MakkitKeys.setup()


        // Remap toolbar activators to '[' and ']'. These are rarely used and the player can view the controls
        // If they wish to see the new bindings.
        KeyRemapper.remap("key.saveToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET)
        KeyRemapper.remap("key.loadToolbarActivator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET)

        Events.DrawScreenEvent.Dispatcher.register(::onDrawScreen)
        Events.MouseScrollEvent.Dispatcher.register(::onScroll)
    }

    private fun onScroll(e: Events.MouseScrollEvent) {
        val reg = getOrCreateRegion()
        reg.tryScrollFace(e.amount)

        if (MakkitKeys.multiPalette.isDown) {
            val holding = MinecraftClient.getInstance().player?.mainHandStack
            val slot = MinecraftClient.getInstance().player?.inventory?.selectedSlot

            if (holding != null && slot != null) {
                ClientPalette.addToPalette(holding, slot)
            } else {
                ClientPalette.clearPalette()
            }

        } else {
            ClientPalette.clearPalette()
        }


    }

    private fun onDrawScreen(e: Events.DrawScreenEvent) {
        // RenderHelper state
        RenderHelper.setState(e.matrices, e.tickDelta, e.camera, e.buffers, e.matrix)

        for (handler in MakkitKeys.keyHandlers) {
            handler.update()
        }

        InputState.update()

        RenderHelper.drawInWorld {
            region?.update()
            region?.draw()

            drawRemoteRegions()
        }
    }

    companion object {

        var config = MakkitConfig.load()

        fun drawRemoteRegions() {
            for (entry in remoteBoxMap) {
                entry.value.draw(
                        colorFill = RenderColor.PINK.toAlpha(.2f),
                        colorEdge = RenderColor.PINK.toAlpha(.2f)
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

        var region: EditRegion? = EditRegion().apply {
            moveTo(0, 0, 0, 2, 3, 4)
        }

    }

}