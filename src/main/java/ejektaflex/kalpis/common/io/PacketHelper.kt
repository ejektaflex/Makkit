package ejektaflex.kalpis.common.io

import ejektaflex.kalpis.common.world.WorldEditor
import ejektaflex.kalpis.common.world.WorldOperation
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.Blocks
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier


object PacketHelper {



    init {
        ClientSidePacketRegistry.INSTANCE.apply {

            register(Identifier("kedit", "")) { context, data ->

                val intent = EditIntent(data)

                context.taskQueue.execute {
                    WorldEditor.handleNetworkOperation(context.player as ServerPlayerEntity, intent)
                }
            }

        }


    }


}