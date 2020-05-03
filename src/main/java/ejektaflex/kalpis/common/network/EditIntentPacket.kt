package ejektaflex.kalpis.common.network

import ejektaflex.kalpis.common.world.WorldEditor
import ejektaflex.kalpis.common.world.WorldOperation
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class EditIntentPacket(
        var start: BlockPos = BlockPos(0, 0, 0),
        var end: BlockPos = BlockPos(0, 0, 0),
        var side: Direction = Direction.NORTH,
        var op: WorldOperation = WorldOperation.FILL,
        var palette: List<ItemStack> = listOf()
) : Packet<ServerPlayPacketListener> {

    override fun apply(listener: ServerPlayPacketListener) {
        val player = (listener as ServerPlayNetworkHandler).player

        player.server.execute {
            WorldEditor.handleNetworkOperation(player as ServerPlayerEntity, this)
        }
    }

    override fun write(buf: PacketByteBuf) {
        buf.apply {
            writeBlockPos(start)
            writeBlockPos(end)
            writeInt(side.ordinal)
            writeInt(op.ordinal)
            writeInt(palette.size)
            for (item in palette) {
                writeItemStack(item)
            }
        }
    }

    override fun read(buf: PacketByteBuf) {
        start = buf.readBlockPos()
        end = buf.readBlockPos()
        side = enumValues<Direction>()[buf.readInt()]
        op = enumValues<WorldOperation>()[buf.readInt()]
        palette = mutableListOf<ItemStack>().apply {
            val paletteSize = buf.readInt()
            for (i in 0 until paletteSize) {
                add(
                        buf.readItemStack()
                )
            }
        }
    }

}