package io.ejekta.makkit.common.network.pakkit

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ServerBoundPakkit : Pakkit {
    fun sendToServer() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(getId(), write())
    }
}