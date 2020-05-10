package ejektaflex.makkit.client

import ejektaflex.makkit.client.config.MakkitConfig
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.InputState
import ejektaflex.makkit.client.editor.input.MakkitKeys
import ejektaflex.makkit.client.event.Events
import ejektaflex.makkit.client.keys.KeyRemapper
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.client.render.RenderColor
import ejektaflex.makkit.client.render.RenderHelper
import ejektaflex.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import ejektaflex.makkit.common.network.pakkits.client.FocusRegionPacket
import net.fabricmc.api.ClientModInitializer
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