package io.ejekta.makkit.common

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import io.ejekta.makkit.common.network.pakkits.server.ClipboardIntentPacket
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import net.fabricmc.api.ModInitializer

class MakkitCommon : ModInitializer {
    override fun onInitialize() {

        // Serverbound packets
        EditWorldPacket.registerC2S()
        EditHistoryPacket.registerC2S()
        ShadowBoxUpdatePacket.registerC2S()
        ClipboardIntentPacket.registerC2S()


        Events.ServerDisconnectEvent.Dispatcher.register(::onServerPlayerDisconnect)

        println("Common init")
    }

    private fun onServerPlayerDisconnect(e: Events.ServerDisconnectEvent) {
        for (player in e.player.world.players) {
            ShadowBoxShowPacket(uid = e.player.uuidAsString, disconnect = true).sendToClient(player)
        }
    }

    companion object {
        const val ID = "makkit"
    }
}