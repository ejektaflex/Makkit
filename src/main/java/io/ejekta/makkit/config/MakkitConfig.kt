package io.ejekta.makkit.config

import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.Modifier
import me.shedaniel.clothconfig2.api.ModifierKeyCode
import me.shedaniel.clothconfig2.impl.builders.KeyCodeBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

object MakkitConfig {

    fun onSave() {

    }

    fun buildScreen(): Screen {
        val builder =  ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(Text.literal("Makkit"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val generalCat = builder.getOrCreateCategory(Text.literal("General"))

        val keyCode = InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, -1)

        generalCat.addEntry(
            creator.startKeyCodeField(
                Text.literal("Doot"),
                keyCode
            ).setDefaultValue(
                keyCode
            ).build()
        )

        generalCat.addEntry(
            creator.startModifierKeyCodeField(
                Text.literal("Blah"),
                ModifierKeyCode.of(
                    InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, -1),
                    Modifier.of(false, false, true)
                )
            ).build()
        )

        return builder.build()
    }

}