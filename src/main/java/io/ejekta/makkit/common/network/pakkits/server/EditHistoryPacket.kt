package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.enums.UndoRedoMode
import kotlinx.serialization.Serializable
import net.minecraft.network.packet.CustomPayload

@Serializable
data class EditHistoryPacket(var mode: UndoRedoMode = UndoRedoMode.UNDO) : KambrikMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        NetworkHandler.handleUndoRedo(ctx.player, this)
    }

    override fun getId() = ID

    companion object {
        val ID = CustomPayload.id<EditHistoryPacket>("edit_history")
    }
}