package io.ejekta.makkit.common

import io.ejekta.kambrik.Kambrik
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import io.ejekta.makkit.common.network.pakkits.server.ClipboardIntentPacket
import io.ejekta.makkit.common.network.pakkits.server.EditHistoryPacket
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import kotlinx.serialization.modules.SerializersModule
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class MakkitCommon : ModInitializer {


    companion object {
        const val ID = "makkit"

        fun onServerPlayerDisconnect(e: Events.ServerDisconnectEvent) {
            for (player in e.player.world.players) {
                ShadowBoxShowPacket(uid = e.player.uuidAsString, disconnect = true).sendToClient(player as ServerPlayerEntity)
            }
        }
    }

    override fun onInitialize() {

//        Kambrik.Message.addSerializerModule(
//            SerializersModule {
//                //contextual(WorldOperation::class, WorldOperation.serializer())
//            }
//        )

        // Serverbound packets
        Kambrik.Message.registerServerMessage(EditWorldPacket.serializer(), EditWorldPacket::class, Identifier(ID, "edit_world"))
        Kambrik.Message.registerServerMessage(EditHistoryPacket.serializer(), EditHistoryPacket::class, Identifier(ID, "edit_history"))
        Kambrik.Message.registerServerMessage(ShadowBoxUpdatePacket.serializer(), ShadowBoxUpdatePacket::class, Identifier(ID, "shadow_box_update"))
        Kambrik.Message.registerServerMessage(ClipboardIntentPacket.serializer(), ClipboardIntentPacket::class, Identifier(ID, "clipboard_intent"))

        Events.ServerDisconnectEvent.Dispatcher.register(::onServerPlayerDisconnect)

        println("Common init")
    }


}