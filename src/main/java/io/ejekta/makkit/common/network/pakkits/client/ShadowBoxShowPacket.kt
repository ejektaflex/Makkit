package io.ejekta.makkit.common.network.pakkits.client

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.network.pakkit.ClientBoundPakkit
import io.ejekta.makkit.common.network.pakkit.ClientSidePakkitHandler
import io.ejekta.makkit.common.network.pakkits.both.BoxPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/*
When received by the client, will add said box to the list of remote boxes that should be
displayed from other players
 */
class ShadowBoxShowPacket(
        val localPacket: ShadowBoxUpdatePacket = ShadowBoxUpdatePacket(),
        var uid: String = "",
        var disconnect: Boolean = false
) : BoxPacket(ID, localPacket.box), ClientBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun write(): PacketByteBuf {
        return super.write().apply {
            writeString(uid)
        }
    }

    override fun read(buf: PacketByteBuf) {
        super.read(buf)
        uid = buf.readString()
    }

    companion object : ClientSidePakkitHandler {
        val ID = Identifier(MakkitCommon.ID, "remote_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ShadowBoxShowPacket(buffer)
            context.taskQueue.execute {

                if (pakkit.disconnect) {
                    if (pakkit.uid in MakkitClient.remoteBoxMap) {
                        MakkitClient.remoteBoxMap.remove(pakkit.uid)
                    }
                } else {
                    println("Adding remote box w/id ${pakkit.uid} to draw map")
                    MakkitClient.remoteBoxMap[pakkit.uid] = RenderBox(
                            pakkit.box
                    )
                }



            }
        }

    }

}