package io.ejekta.makkit.common

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

        println("Common init")
    }

    companion object {
        const val ID = "makkit"
    }
}