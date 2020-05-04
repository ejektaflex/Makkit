package ejektaflex.makkit.common.network.pakkit

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface PakkitHandler {

    fun getId(): Identifier

    fun run(context: PacketContext, buffer: PacketByteBuf)

}