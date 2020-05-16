package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.common.ext.identifier
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack

object ItemPalette {

    private val stacks = mutableMapOf<String, ItemStack>()

    fun addToPalette(stack: ItemStack) {
        println("Adding to palette: $stack")
        stacks[stack.identifier.toString()] = stack
    }

    fun hasStack(stack: ItemStack): Boolean {
        return stack.identifier.toString() in stacks.keys
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