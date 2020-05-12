package ejektaflex.makkit.common.network.pakkits.server

import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.network.pakkits.both.BoxPacket
import ejektaflex.makkit.common.editor.NetworkHandler
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class ShadowBoxUpdatePacket(
        override var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
) : BoxPacket(ID, box), ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    companion object : ServerSidePakkitHandler {

        val ID = Identifier("makkit", "local_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ShadowBoxUpdatePacket(buffer)
            context.taskQueue.execute {
                println("Sending remote block preview to other clients")
                NetworkHandler.redirectRemoteBoxPreview(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}