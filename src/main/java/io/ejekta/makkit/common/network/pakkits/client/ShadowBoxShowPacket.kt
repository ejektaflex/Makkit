package io.ejekta.makkit.common.network.pakkits.client

import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.render.AnimBox
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.Box

/*
When received by the client, will add said box to the list of remote boxes that should be
displayed from other players
 */
@Serializable
class ShadowBoxShowPacket(
        val box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var uid: String,
        var disconnect: Boolean = false
) : ClientMsg() {

    override fun onClientReceived(ctx: MsgContext) {
        if (disconnect) {
            if (uid in MakkitClient.remoteBoxMap) {
                MakkitClient.remoteBoxMap.remove(uid)
            }
        } else {
            if (uid in MakkitClient.remoteBoxMap) {
                MakkitClient.remoteBoxMap[uid]!!.resize(box)
            } else {
                MakkitClient.remoteBoxMap[uid] = AnimBox(box).apply {
                    shrinkToCenter()
                }
            }
        }
    }

}