package io.ejekta.makkit.common.ext

import net.minecraft.block.BlockState
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

fun BlockState.rotated(times: Int): BlockState {
    var state = this

    if (state.contains(Properties.FACING)) {
        state = state.with(
                Properties.FACING,
                state.get(Properties.FACING)
                        .rotatedClockwise(times))
    }

    if (state.contains(Properties.HORIZONTAL_FACING)) {
        state = state.with(
                Properties.HORIZONTAL_FACING,
                state.get(Properties.HORIZONTAL_FACING)
                        .rotatedClockwise(times)
        )
    }

    return state
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

    if (state.contains(Properties.FACING) && state.get(Properties.FACING).axis == axis) {
        state = state.with(
                Properties.FACING, state.get(Properties.FACING).opposite
        )
    }

    if (state.contains(Properties.SLAB_TYPE) && axis == Direction.Axis.Y) {
        var type = state.get(Properties.SLAB_TYPE)
        type = when (type) {
            SlabType.BOTTOM -> SlabType.TOP
            SlabType.TOP -> SlabType.BOTTOM
            else -> type
        }
        state = state.with(Properties.SLAB_TYPE, type)
    }

    if (state.contains(Properties.HORIZONTAL_FACING) && state.get(Properties.HORIZONTAL_FACING).axis == axis) {
        state = state.with(
                Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING).opposite
        )
    }

    return state
}


