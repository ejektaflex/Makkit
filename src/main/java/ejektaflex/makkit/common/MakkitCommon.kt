package ejektaflex.makkit.common

import ejektaflex.makkit.common.network.pakkits.EditHistoryPacket
import ejektaflex.makkit.common.network.pakkits.EditWorldPacket
import net.fabricmc.api.ModInitializer

class MakkitCommon : ModInitializer {
    override fun onInitialize() {
        EditWorldPacket.registerC2S()
        EditHistoryPacket.registerC2S()
        println("Common init")
    }
}