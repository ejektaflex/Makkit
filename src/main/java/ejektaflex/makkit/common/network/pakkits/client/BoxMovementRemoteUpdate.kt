package ejektaflex.makkit.common.network.pakkits.client

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.render.RenderBox
import ejektaflex.makkit.common.enum.UndoRedoMode
import ejektaflex.makkit.common.ext.vec3d
import ejektaflex.makkit.common.network.pakkit.ClientBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ClientSidePakkitHandler
import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.network.pakkits.both.BoxPacket
import ejektaflex.makkit.common.world.WorldEditor
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class BoxMovementRemoteUpdate(
        start: BlockPos,
        end: BlockPos
) : BoxPacket(start, end) {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID



    companion object : ClientSidePakkitHandler {
        val ID = Identifier("makkit", "remote_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = BoxMovementRemoteUpdate(buffer)
            context.taskQueue.execute {

                println("Adding remote box to draw map")
                val uid = context.player.uuidAsString
                MakkitClient.remoteBoxMap[uid] = RenderBox(
                        pakkit.start.vec3d(),
                        pakkit.end.vec3d()
                )

            }
        }

    }

}