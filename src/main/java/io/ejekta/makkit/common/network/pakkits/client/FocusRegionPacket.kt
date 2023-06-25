package io.ejekta.makkit.common.network.pakkits.client

import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.makkit.client.MakkitClient
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.Box

/*
    Sent to a client when we want their editor area to focus on a certain region
 */
@Serializable
data class FocusRegionPacket(
        var box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {
        super.onClientReceived(ctx)
        if (MakkitClient.historyHighlighting) {
            MakkitClient.getOrCreateRegion().apply {
                selection = box
            }
        }
    }


}