package io.ejekta.makkit.common.ext

import net.minecraft.block.BlockState
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

    if (state.contains(Properties.FACING)) {
        state = state.with(
                Properties.FACING, dir
        )
    }

    if (state.contains(Properties.HORIZONTAL_FACING)) {
        state = state.with(
                Properties.HORIZONTAL_FACING, dir
        )
    }

    return state
}


