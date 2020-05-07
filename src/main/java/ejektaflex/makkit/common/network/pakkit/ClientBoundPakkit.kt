package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.Pakkit
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity

interface ClientBoundPakkit : Pakkit {
    fun sendToClient(player: PlayerEntity) {
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, getId(), write())
    }
}