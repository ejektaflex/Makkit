package io.ejekta.makkit.common.ext

import net.minecraft.block.BlockState
import net.minecraft.block.enums.BlockHalf
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.Direction

fun BlockState.rotated(times: Int): BlockState {
    var state = this

    var rot = BlockRotation.NONE

    for (i in 0 until times) {
        rot = rot.rotate(BlockRotation.CLOCKWISE_90)
    }

    return state.rotate(rot)

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


    fun flipBoolProp(prop: BooleanProperty, opposite: BooleanProperty): BlockState {
        if (prop in state && opposite in state) {
            if ((state[prop] && !state[opposite]) || (!state[prop] && state[opposite])) {
                val newProp = !state[prop]
                val newOpp = !state[opposite]
                return state.with(prop, newProp).with(opposite, newOpp)
            }
        }
        return state
    }

    // Fences & Walls & Other Stuff
    state = when (axis) {
        Direction.Axis.X -> flipBoolProp(Properties.EAST, Properties.WEST)
        Direction.Axis.Y -> flipBoolProp(Properties.UP, Properties.DOWN)
        Direction.Axis.Z -> flipBoolProp(Properties.NORTH, Properties.SOUTH)
    }

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

    if (Properties.FACING in state && state[Properties.FACING].axis == axis) {
        state = state.with(
                Properties.FACING, state[Properties.FACING].opposite
        )
    }

    if (Properties.HORIZONTAL_FACING in state && state[Properties.HORIZONTAL_FACING].axis == axis) {
        state = state.with(
                Properties.HORIZONTAL_FACING, state[Properties.HORIZONTAL_FACING].opposite
        )
    }

    return state
}


