package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.common.ext.roundToVec3i
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object CopyHelper {

    fun getLocalAxisSize(copyBox: Box, face: Direction): BlockPos {
        return when (face.axis) {
            Direction.Axis.X -> BlockPos(Vec3d(copyBox.xLength, copyBox.yLength, copyBox.zLength).roundToVec3i())
            Direction.Axis.Z -> BlockPos(Vec3d(copyBox.zLength, copyBox.yLength, copyBox.xLength).roundToVec3i())
            else -> throw Exception("This shouldn't happen!")
        }
    }

    fun getLocalAxisStartPos(copyBox: Box, face: Direction): BlockPos {
        return when (face) {
            Direction.NORTH -> BlockPos(Vec3d(copyBox.maxX - 1, copyBox.minY, copyBox.minZ).roundToVec3i())
            Direction.EAST -> BlockPos(Vec3d(copyBox.maxX - 1, copyBox.minY, copyBox.maxZ - 1).roundToVec3i())
            Direction.SOUTH -> BlockPos(Vec3d(copyBox.minX, copyBox.minY, copyBox.maxZ - 1).roundToVec3i())
            Direction.WEST -> BlockPos(Vec3d(copyBox.minX, copyBox.minY, copyBox.minZ).roundToVec3i())
            else -> throw Exception("Cannot paste when look vector is up or down")
        }
    }

}