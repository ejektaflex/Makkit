package io.ejekta.makkit.client.editor.input

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.IEditor
import io.ejekta.makkit.common.MakkitCommon
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableText

class KeyStateHandler(val id: String, var binding: ModifierKeyCode) : IEditor {

    private var wasJustReleased = false

    val isScreenOpen: Boolean
        get() = MinecraftClient.getInstance().currentScreen != null

    var isDown = false
        private set

    val name: MutableText
        get() = TranslatableText("${MakkitCommon.ID}.$id")

    val shortName: MutableText
        get() = TranslatableText("${MakkitCommon.ID}.$id.short")

    fun setKeyDown(func: () -> Unit) {
        onKeyDown = func
    }

    fun setKeyUp(func: () -> Unit) {
        onKeyUp = func
    }

    fun recentlyMatchedOtherKeys(): Boolean {
        val matchedKeys = MakkitClient.config.keys.filter {
            it.binding.keyCode == binding.keyCode
        }
        return matchedKeys.any {
            val passes = (it.isDown && it != this) || it.wasJustReleased
            if (passes) {
                it.wasJustReleased = false
            }
            passes
        }
    }

    private var onKeyDown = {}

    private var onKeyUp = {}

    override fun update() {
        // Try to start dragging
        if (!isScreenOpen && !isDown && (binding.matchesCurrentKey() || binding.matchesCurrentMouse())) {
            // Hacky way to prevent Z getting hit after, say, CTRL+Z then letting go of Z
            if (!recentlyMatchedOtherKeys()) {
                isDown = true
                onKeyDown()
            }
        }

        val fakeKey = ModifierKeyCode.of(binding.keyCode, Modifier.none())

        // Try to stop dragging
        if (isDown && (!binding.matchesCurrentKey()
                        && !fakeKey.matchesCurrentKey()
                        && !binding.matchesCurrentMouse())
        ) {
            wasJustReleased = true
            onKeyUp()
            isDown = false
        }

    }

}