package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.network.pakkit.ServerBoundPakkit
import io.ejekta.makkit.common.network.pakkit.ServerSidePakkitHandler
import io.ejekta.makkit.common.network.pakkits.both.BoxPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

class ShadowBoxUpdatePacket(
        override var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var disconnect: Boolean = false
) : BoxPacket(ID, box), ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun write(): PacketByteBuf {
        return super.write().apply {
            writeBoolean(disconnect)
        }
    }

    override fun read(buf: PacketByteBuf) {
        super.read(buf).apply {
            disconnect = buf.readBoolean()
        }
    }

    companion object : ServerSidePakkitHandler {

        val ID = Identifier(MakkitCommon.ID, "local_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ShadowBoxUpdatePacket(buffer)
            context.taskQueue.execute {
                //println("Sending remote block preview to other clients")
                NetworkHandler.redirectRemoteBoxPreview(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }

}