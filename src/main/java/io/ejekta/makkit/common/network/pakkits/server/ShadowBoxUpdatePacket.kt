package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.Box


@Serializable
class ShadowBoxUpdatePacket(
        var box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var disconnect: Boolean = false
) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        NetworkHandler.redirectRemoteBoxPreview(ctx.player, this)
    }
}