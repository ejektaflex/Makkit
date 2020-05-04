package ejektaflex.makkit.common.network

import ejektaflex.makkit.client.network.EditIntentPacket
import ejektaflex.makkit.common.network.pakkit.ClientPakkitHandler
import ejektaflex.makkit.common.world.WorldEditor
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

object EditIntentPacketHandler : ClientPakkitHandler {

    override fun getId() = EditIntentPacket.ID

    override fun run(context: PacketContext, buffer: PacketByteBuf) {
        println("Executing packet ${getId()}")
        val intent = EditIntentPacket(buffer)
        context.taskQueue.execute {
            WorldEditor.handleNetworkOperation(context.player as ServerPlayerEntity, intent)
        }
    }
}