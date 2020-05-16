package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.common.ext.identifier
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack

object ClientPalette {

    private val stacks = mutableMapOf<Int, ItemStack>()

    fun addToPalette(stack: ItemStack, slot: Int) {
        println("Adding to palette: $stack")
        stacks[slot] = stack
    }


    fun hasStack(slot: Int): Boolean {
        return slot in stacks.keys
    }

    fun getSafePalette(): List<ItemStack> {
        return when (hasAnyItems()) {
            true -> stacks.values.filter {
                it.item is BlockItem || it.item is AirBlockItem
            }
            else -> listOf(MinecraftClient.getInstance().player!!.mainHandStack)
        }
    }

    fun hasAnyItems(): Boolean {
        return stacks.values.isNotEmpty()
    }

    fun clearPalette() {
        println("Clearing stack")
        stacks.clear()
    }

}