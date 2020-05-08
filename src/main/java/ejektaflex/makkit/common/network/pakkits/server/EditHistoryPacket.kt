package ejektaflex.makkit.common.network.pakkits.server

import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.editor.NetworkHandler
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
            writeInt(mode.ordinal)
        }
    }

    override fun read(buf: PacketByteBuf) {
        mode = enumValues<UndoRedoMode>()[buf.readInt()]
    }

    companion object : ServerSidePakkitHandler {
        val ID = Identifier("makkit", "edit_history")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = EditHistoryPacket(buffer)
            context.taskQueue.execute {
                NetworkHandler.handleUndoRedo(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}