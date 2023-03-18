package io.ejekta.makkit.common.editor.data

import io.ejekta.makkit.client.mixin.ItemBucketAccessor
import io.ejekta.makkit.common.ext.inDirection
import io.ejekta.makkit.common.ext.weightedRandom
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.item.*
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import java.util.*
import kotlin.math.max

class BlockPalette(val action: EditAction) {

    fun isEmpty(): Boolean {
        return blocks.isEmpty()
    }



    private val blocks = parse(action.stacks)

    fun getRandom(): BlockState {

        val pickedBlock = when (action.options.weightedPalette) {
            true -> blocks.weightedRandom()
            else -> blocks.keys.random()
        }

        return getDefaultState(pickedBlock).inDirection(
                if (action.options.randomRotate) Direction.random(random) else action.direction
        )
    }

    companion object {
        private val random = Random.create()

        fun getDefaultState(block: Block): BlockState {
            var state = block.defaultState

            // Water default state should be filled
            if (block is FluidBlock) {
                state = state.with(Properties.LEVEL_15, 0)
            }

            return state
        }

        private fun parse(items: List<ItemStack>): Map<Block, Int> {
            val proto = mutableMapOf<Block, Int>()

            fun increment(block: Block, stack: ItemStack) {
                val oldCount: Int = proto.getOrDefault(block, 0)
                proto[block] = oldCount + max(stack.count, 1)
            }

            for (stack in items) {
                val block = test(stack)
                block?.let {
                    increment(it, stack)
                }
            }

            return proto
        }

        fun testBlockOnly(stack: ItemStack): Block? {
            val item = stack.item
            return when(item) {
                is BlockItem -> item.block
                else -> null
            }
        }

        fun test(stack: ItemStack): Block? {
            val item = stack.item
            return testBlockOnly(stack) ?: when (item) {
                is AirBlockItem, Items.STICK -> Blocks.AIR
                is BucketItem -> (item as ItemBucketAccessor).fluid.defaultState.blockState.block
                else -> null
            }
        }

    }



}