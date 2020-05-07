package ejektaflex.makkit.common.network.pakkits.server

import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.world.WorldEditor
import ejektaflex.makkit.common.world.WorldOperation
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class EditWorldPacket(
        var start: BlockPos = BlockPos(0, 0, 0),
        var end: BlockPos = BlockPos(0, 0, 0),
        var side: Direction = Direction.NORTH,
        var op: WorldOperation = WorldOperation.SET,
        var palette: List<ItemStack> = listOf()
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
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

    companion object : ServerSidePakkitHandler {
        val ID = Identifier("makkit", "edit_intent")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val intent = EditWorldPacket(buffer)
            context.taskQueue.execute {
                WorldEditor.handleEdit(context.player as ServerPlayerEntity, intent)
            }
        }

    }

}