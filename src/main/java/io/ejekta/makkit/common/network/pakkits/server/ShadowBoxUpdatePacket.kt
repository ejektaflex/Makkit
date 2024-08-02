package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.math.Box


@Serializable
class ShadowBoxUpdatePacket(
        var box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var disconnect: Boolean = false
) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        NetworkHandler.redirectRemoteBoxPreview(ctx.player, this)
    }

    override fun getId() = ID

    companion object {
        val ID = CustomPayload.id<ShadowBoxUpdatePacket>("shadow_box_update")
    }
}