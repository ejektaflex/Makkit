package ejektaflex.makkit.common.network.pakkits.both

import ejektaflex.makkit.common.network.pakkit.Pakkit
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

abstract class BoxPacket(
        val packetId: Identifier,
        open var start: BlockPos = BlockPos(0, 0, 0),
        open var end: BlockPos = BlockPos(1, 1, 1)
) : Pakkit {

    final override fun getId() = packetId

    final override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeBlockPos(start)
            writeBlockPos(end)
        }
    }

    final override fun read(buf: PacketByteBuf) {
        start = buf.readBlockPos()
        end = buf.readBlockPos()
    }

}