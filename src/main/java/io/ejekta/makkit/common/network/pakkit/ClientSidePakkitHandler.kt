package io.ejekta.makkit.common.network.pakkit

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ClientSidePakkitHandler : PakkitHandler {
    fun registerS2C() {
        ClientSidePacketRegistry.INSTANCE.register(getId(), ::run)
    }
}