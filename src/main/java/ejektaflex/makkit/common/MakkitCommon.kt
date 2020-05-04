package ejektaflex.makkit.common

import ejektaflex.makkit.common.network.pakkits.EditHistoryPacket
import ejektaflex.makkit.common.network.pakkits.EditIntentPacket
import net.fabricmc.api.ModInitializer

class MakkitCommon : ModInitializer {
    override fun onInitialize() {
        EditIntentPacket.registerC2S()
        EditHistoryPacket.registerC2S()
        println("Common init")
    }
}