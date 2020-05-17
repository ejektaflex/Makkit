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
import java.util.*
import kotlin.math.max

class BlockPalette(inItems: List<ItemStack>, val weighted: Boolean, val randomRotate: Boolean, val defaultDir: Direction) {

    fun isEmpty(): Boolean {
        return blocks.isEmpty()
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

    private val blocks = parse(inItems)

    fun getRandom(): BlockState {

        val pickedBlock = when (weighted) {
            true -> blocks.weightedRandom()
            else -> blocks.keys.random()
        }

        return getDefaultState(pickedBlock).inDirection(
                if (randomRotate) Direction.random(random) else defaultDir
        )
    }

    companion object {
        private val random = Random()

        fun getDefaultState(block: Block): BlockState {
            var state = block.defaultState

            // Water default state should be filled
            if (block is FluidBlock) {
                state = state.with(Properties.LEVEL_15, 0)
            }

            return state
        }

        fun test(stack: ItemStack): Block? {
            val item = stack.item
            when (item) {
                is BlockItem -> {
                    return item.block
                }
                is AirBlockItem, Items.STICK -> {
                    return Blocks.AIR
                }
                is BucketItem -> {
                    return (item as ItemBucketAccessor).fluid.defaultState.blockState.block
                }
            }
            return null
        }

    }



}