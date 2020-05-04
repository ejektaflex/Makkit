package ejektaflex.makkit.common.network.pakkits

import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkit.ClientPakkit
import ejektaflex.makkit.common.network.pakkit.ClientPakkitHandler
import ejektaflex.makkit.common.world.WorldEditor
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class EditHistoryPacket(
        var mode: UndoRedoMode = UndoRedoMode.UNDO
) : ClientPakkit {

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

    companion object : ClientPakkitHandler {
        val ID = Identifier("makkit", "edit_history")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = EditHistoryPacket(buffer)
            context.taskQueue.execute {
                WorldEditor.handleUndoRedo(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}