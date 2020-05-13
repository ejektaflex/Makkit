package io.ejekta.makkit.common.network.pakkits.client

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.render.RenderBox
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
        localPacket: ShadowBoxUpdatePacket = ShadowBoxUpdatePacket()
) : BoxPacket(ID, localPacket.box), ClientBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    companion object : ClientSidePakkitHandler {
        val ID = Identifier("makkit", "remote_movement")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ShadowBoxShowPacket(buffer)
            context.taskQueue.execute {

                println("Adding remote box to draw map")
                val uid = context.player.uuidAsString
                MakkitClient.remoteBoxMap[uid] = RenderBox(
                        pakkit.box
                )

            }
        }

    }

}