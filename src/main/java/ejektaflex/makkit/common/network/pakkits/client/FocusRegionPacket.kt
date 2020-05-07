package ejektaflex.makkit.common.network.pakkits.client

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.network.pakkit.ClientBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ClientSidePakkitHandler
import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.world.WorldEditor
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class FocusRegionPacket(
        var start: BlockPos = BlockPos(0, 0, 0),
        var end: BlockPos = BlockPos(1, 1, 1)
) : ClientBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeBlockPos(start)
            writeBlockPos(end)
        }
    }

    override fun read(buf: PacketByteBuf) {
        start = buf.readBlockPos()
        end = buf.readBlockPos()
    }

    companion object : ClientSidePakkitHandler {
        val ID = Identifier("makkit", "focus_region")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = FocusRegionPacket(buffer)
            context.taskQueue.execute {
                if (MakkitClient.config.historyHighlighting) {
                    MakkitClient.getOrCreateRegion().apply {
                        area.box = Box(pakkit.start, pakkit.end)
                    }
                }
            }
        }

    }

}