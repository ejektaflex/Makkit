package ejektaflex.makkit.common

import ejektaflex.makkit.common.network.pakkits.server.BoxPreviewLocalPacket
import ejektaflex.makkit.common.network.pakkits.server.EditHistoryPacket
import ejektaflex.makkit.common.network.pakkits.server.EditWorldPacket
import net.fabricmc.api.ModInitializer

class MakkitCommon : ModInitializer {
    override fun onInitialize() {

        // Serverbound packets
        EditWorldPacket.registerC2S()
        EditHistoryPacket.registerC2S()
        BoxPreviewLocalPacket.registerC2S()

        println("Common init")
    }

    companion object {
        const val ID = "makkit"
    }
}