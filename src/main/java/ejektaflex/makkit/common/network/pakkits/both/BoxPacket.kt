package ejektaflex.makkit.common.network.pakkits.both

import ejektaflex.makkit.common.ext.readIntBox
import ejektaflex.makkit.common.ext.writeIntBox
import ejektaflex.makkit.common.network.pakkit.Pakkit
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

abstract class BoxPacket(
        val packetId: Identifier,
        open var box: Box
) : Pakkit {

    final override fun getId() = packetId

    final override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeIntBox(box)
        }
    }

    final override fun read(buf: PacketByteBuf) {
        box = buf.readIntBox()
    }

}