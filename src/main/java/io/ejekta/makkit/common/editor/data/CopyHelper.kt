package io.ejekta.makkit.common.editor.data

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

object CopyHelper {

    fun getLocalAxisSize(copyBox: Box, face: Direction): BlockPos {
        return when (face.axis) {
            Direction.Axis.X -> BlockPos(copyBox.xLength, copyBox.yLength, copyBox.zLength)
            Direction.Axis.Z -> BlockPos(copyBox.zLength, copyBox.yLength, copyBox.xLength)
            else -> throw Exception("This shouldn't happen!")
        }
    }

    fun getLocalAxisStartPos(copyBox: Box, face: Direction): BlockPos {
        return when (face) {
            Direction.NORTH -> BlockPos(copyBox.maxX - 1, copyBox.minY, copyBox.minZ)
            Direction.EAST -> BlockPos(copyBox.maxX - 1, copyBox.minY, copyBox.maxZ - 1)
            Direction.SOUTH -> BlockPos(copyBox.minX, copyBox.minY, copyBox.maxZ - 1)
            Direction.WEST -> BlockPos(copyBox.minX, copyBox.minY, copyBox.minZ)
            else -> throw Exception("Cannot paste when look vector is up or down")
        }
    }

}