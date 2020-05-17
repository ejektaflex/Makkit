package io.ejekta.makkit.client.editor.input

import net.minecraft.client.MinecraftClient
import net.minecraft.item.AirBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack

object ClientPalette {

    private val stacks = mutableSetOf<Int>()

    fun addToPalette(slot: Int) {
        //println("Adding to palette: $stack")
        stacks.add(slot)
    }


    fun hasStack(slot: Int): Boolean {
        return slot in stacks
    }

    private fun getStacks(): List<ItemStack> {
        return stacks.mapNotNull {
            MinecraftClient.getInstance().player?.inventory?.getStack(it)
        }
    }

    fun getSafePalette(): List<ItemStack> {
        return when (hasAnyItems()) {
            true -> getStacks()
            else -> listOf(MinecraftClient.getInstance().player!!.mainHandStack)
        }
    }

    fun hasAnyItems(): Boolean {
        return getStacks().isNotEmpty()
    }

    fun clearPalette() {
        println("Clearing stack")
        stacks.clear()
    }

}