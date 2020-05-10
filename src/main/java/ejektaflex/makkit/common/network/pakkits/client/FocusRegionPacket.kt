package ejektaflex.makkit.common.network.pakkits.client

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.common.network.pakkit.ClientBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ClientSidePakkitHandler
import ejektaflex.makkit.common.network.pakkits.both.BoxPacket
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

/*
    Sent to a client when we want their editor area to focus on a certain region
 */
class FocusRegionPacket(
        override var start: BlockPos = BlockPos(0, 0, 0),
        override var end: BlockPos = BlockPos(1, 1, 1)
) : BoxPacket(ID, start, end), ClientBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
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