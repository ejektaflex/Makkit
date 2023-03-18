package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.enums.UndoRedoMode
import kotlinx.serialization.Serializable

@Serializable
data class EditHistoryPacket(var mode: UndoRedoMode = UndoRedoMode.UNDO) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        NetworkHandler.handleUndoRedo(ctx.player, this)
    }
}