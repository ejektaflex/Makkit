package ejektaflex.kalpis.common.io

import ejektaflex.kalpis.common.world.WorldOperation
import io.netty.buffer.Unpooled
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class EditIntent(
        var start: BlockPos = BlockPos(0, 0, 0),
        var end: BlockPos = BlockPos(0, 0, 0),
        var side: Direction = Direction.NORTH,
        var op: WorldOperation = WorldOperation.FILL,
        var palette: List<ItemStack> = listOf()
) {

    constructor(data: PacketByteBuf) : this() {
        start = data.readBlockPos()
        end = data.readBlockPos()
        side = enumValues<Direction>()[data.readInt()]
        op = enumValues<WorldOperation>()[data.readInt()]

        val protoPalette = mutableListOf<ItemStack>()
        val paletteSize = data.readInt()
        for (i in 0 until paletteSize) {
            protoPalette.add(
                    data.readItemStack()
            )
        }
        palette = protoPalette
    }

    fun write(): PacketByteBuf {
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

}