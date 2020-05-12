package ejektaflex.makkit.common.network.pakkits.server

import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.enum.ClipboardMode
import ejektaflex.makkit.common.ext.readIntBox
import ejektaflex.makkit.common.ext.readEnum
import ejektaflex.makkit.common.ext.writeIntBox
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

class ClipboardIntentPacket(
        var mode: ClipboardMode = ClipboardMode.CUT,
        var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun read(buf: PacketByteBuf) {
        mode = buf.readEnum()
        box = buf.readIntBox()
    }

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeInt(mode.ordinal)
            writeIntBox(box)
        }
    }

    companion object : ServerSidePakkitHandler {

        val ID = Identifier("makkit", "clipboard_intent")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ClipboardIntentPacket(buffer)
            context.taskQueue.execute {
                println("Sending remote block preview to other clients")
                //NetworkHandler.redirectRemoteBoxPreview(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }


}