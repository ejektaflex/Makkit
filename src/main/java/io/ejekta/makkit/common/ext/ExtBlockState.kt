package io.ejekta.makkit.common.ext

import net.minecraft.block.BlockState
import net.minecraft.block.enums.BlockHalf
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.Direction

fun BlockState.rotated(times: Int): BlockState {
    var rot = BlockRotation.NONE

    for (i in 0 until times) {
        rot = rot.rotate(BlockRotation.CLOCKWISE_90)
    }

    return rotate(rot)
}

fun BlockState.inDirection(dir: Direction): BlockState {
    var state = this

    if (state.contains(Properties.FACING) && dir.horizontal != -1) {
        state = state.with(
                Properties.FACING, dir.opposite
        )
    }

    if (state.contains(Properties.HORIZONTAL_FACING) && dir.horizontal != -1) {
        state = state.with(
                Properties.HORIZONTAL_FACING, dir.opposite
        )
    }

    return state
}

fun BlockState.flippedOn(axis: Direction.Axis): BlockState {
    var state = this

    val mirror = when (axis) {
        Direction.Axis.X -> BlockMirror.FRONT_BACK
        Direction.Axis.Z -> BlockMirror.LEFT_RIGHT
        Direction.Axis.Y -> BlockMirror.NONE
    }

    state = state.mirror(mirror)

    // Flipping on Y axis specifically
    if (axis == Direction.Axis.Y) {

        // Slabs
        if (Properties.SLAB_TYPE in state) {
            var type = state[Properties.SLAB_TYPE]
            type = when (type) {
                SlabType.BOTTOM -> SlabType.TOP
                SlabType.TOP -> SlabType.BOTTOM
                else -> type
            }
            state = state.with(Properties.SLAB_TYPE, type)
        }

        // Stairs
        if (Properties.BLOCK_HALF in state) {
            var type = state[Properties.BLOCK_HALF]
            type = when (type) {
                BlockHalf.BOTTOM -> BlockHalf.TOP
                BlockHalf.TOP -> BlockHalf.BOTTOM
            }
            state = state.with(Properties.BLOCK_HALF, type)
        }

    }

    return state
}


