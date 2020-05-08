package ejektaflex.makkit.common.network.pakkits.server

import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.world.WorldEditor
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class BoxMovementLocalUpdate(
        var start: BlockPos = BlockPos(0, 0, 0),
        var end: BlockPos = BlockPos(1, 1, 1)
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun read(buf: PacketByteBuf) {
        start = buf.readBlockPos()
        end = buf.readBlockPos()
    }

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeBlockPos(start)
            writeBlockPos(end)
        }
    }

    companion object : ServerSidePakkitHandler {

        val ID = Identifier("makkit", "local_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = BoxMovementLocalUpdate(buffer)
            context.taskQueue.execute {
                println("Sending remote block preview to other clients")
                WorldEditor.redirectRemoteBoxPreview(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}