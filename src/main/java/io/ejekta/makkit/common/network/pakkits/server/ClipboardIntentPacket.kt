package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.enums.BlockMask
import io.ejekta.makkit.common.enums.ClipboardMode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

@Serializable
data class ClipboardIntentPacket(
        var mode: ClipboardMode = ClipboardMode.CUT,
        var face: Direction = Direction.UP,
        var box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var mask: BlockMask = BlockMask.ALL_BLOCKS
) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        NetworkHandler.handleCopyPaste(ctx.player, this)
    }
}