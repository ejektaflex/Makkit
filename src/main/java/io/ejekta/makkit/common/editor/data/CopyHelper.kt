package io.ejekta.makkit.common.editor.data

import net.minecraft.util.math.*

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
            Direction.NORTH -> BlockPos(copyBox.x2 - 1, copyBox.y1, copyBox.z1)
            Direction.EAST -> BlockPos(copyBox.x2 - 1, copyBox.y1, copyBox.z2 - 1)
            Direction.SOUTH -> BlockPos(copyBox.x1, copyBox.y1, copyBox.z2 - 1)
            Direction.WEST -> BlockPos(copyBox.x1, copyBox.y1, copyBox.z1)
            else -> throw Exception("Cannot paste when look vector is up or down")
        }
    }

}