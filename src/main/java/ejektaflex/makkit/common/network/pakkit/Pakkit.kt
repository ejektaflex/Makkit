package ejektaflex.makkit.common.network.pakkit

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface Pakkit {

    fun getId(): Identifier

    fun read(buf: PacketByteBuf)

    fun write(): PacketByteBuf

}