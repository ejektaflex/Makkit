package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.enums.UndoRedoMode
import io.ejekta.makkit.common.ext.readEnum
import io.ejekta.makkit.common.ext.writeEnum
import io.ejekta.makkit.common.network.pakkit.ServerBoundPakkit
import io.ejekta.makkit.common.network.pakkit.ServerSidePakkitHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class EditHistoryPacket(
        var mode: UndoRedoMode = UndoRedoMode.UNDO
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeEnum(mode)
        }
    }

    override fun read(buf: PacketByteBuf) {
        mode = buf.readEnum()
    }

    companion object : ServerSidePakkitHandler {
        val ID = Identifier(MakkitCommon.ID, "edit_history")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = EditHistoryPacket(buffer)
            context.taskQueue.execute {
                NetworkHandler.handleUndoRedo(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}