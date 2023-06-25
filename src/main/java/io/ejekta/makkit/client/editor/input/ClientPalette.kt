package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.editor.data.BlockPalette
import io.ejekta.makkit.common.ext.weightedRandomBy
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

object ClientPalette {

    private val stacks = mutableSetOf<Int>()

    fun addToPalette(slot: Int) {
        stacks.add(slot)
    }

    fun getSelectedSlots(): List<Int> = stacks.toList()

    // Get all slots that correspond with valid block palette blocks
    fun getBlockSlots(): List<Int> {
        return getSelectedSlots().filter { slot ->
            getStack(slot)?.let {
                BlockPalette.testBlockOnly(it) != null
            } == true
        }
    }

    fun getRandomBlockSlot(): Int? {
        val slots = getBlockSlots()

        if (slots.isEmpty()) return null

        return if (MakkitClient.weightedPalette) {
            slots.weightedRandomBy { getStack(this)!!.count }
        } else {
            slots.random()
        }
    }

    private fun getStacks(): List<ItemStack> {
        return stacks.mapNotNull { getStack(it) }
    }

    private fun getStack(slot: Int): ItemStack? {
        return MinecraftClient.getInstance().player?.inventory?.getStack(slot)
    }

    fun hasStack(slot: Int): Boolean {
        return slot in stacks
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
        stacks.clear()
    }

}