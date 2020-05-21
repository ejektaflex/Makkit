package io.ejekta.makkit.common.network.pakkits.both

import io.ejekta.makkit.common.ext.readIntBox
import io.ejekta.makkit.common.ext.writeIntBox
import io.ejekta.makkit.common.network.pakkit.Pakkit
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

abstract class BoxPacket(
        val packetId: Identifier,
        open var box: Box
) : Pakkit {

    final override fun getId() = packetId

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeIntBox(box)
        }
    }

    override fun read(buf: PacketByteBuf) {
        box = buf.readIntBox()
    }

}