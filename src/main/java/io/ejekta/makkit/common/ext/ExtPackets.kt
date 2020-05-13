package io.ejekta.makkit.common.ext

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box


// Reading and writing boxes to packet buffers will cause decimal values to get chopped off
// hence why we call it an IntBox here

fun PacketByteBuf.writeIntBox(box: Box) {
    writeBlockPos(BlockPos(box.getStart()))
    writeBlockPos(BlockPos(box.getEnd()))
}

fun PacketByteBuf.readIntBox(): Box {
    val start = readBlockPos()
    val end = readBlockPos()
    return Box(start, end)
}

fun PacketByteBuf.writeEnum(enum: Enum<*>) {
    writeInt(enum.ordinal)
}

inline fun <reified T : Enum<T>> PacketByteBuf.readEnum(): T {
    return enumValues<T>()[readInt()]
}