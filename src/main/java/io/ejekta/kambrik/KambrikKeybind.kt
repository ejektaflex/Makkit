package io.ejekta.kambrik


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

class KambrikKeybind(
    translation: String,
    type: InputUtil.Type,
    key: Int,
    category: String,
    val realTime: Boolean = false
) : KeyBinding(
    translation,
    type,
    key,
    category
) {

    private var keyDown = {}

    private var keyUp = {}

    private var keyRepeat = {}

    fun onDown(func: () -> Unit) {
        keyDown = func
    }

    fun onUp(func: () -> Unit) {
        keyUp = func
    }

    fun onRepeat(func: () -> Unit) {
        keyRepeat = func
    }

    var isDown = false
        private set

    override fun setPressed(pressed: Boolean) {
        if (pressed && isPressed) {
            keyRepeat()
        }
        super.setPressed(pressed)
    }

    fun update(wasPressed: Boolean) {
        if (!isDown && wasPressed) {
            isDown = wasPressed
            keyDown()
        } else if (isDown && !wasPressed) {
            isDown = wasPressed
            keyUp()
            isPressed = false
        }
    }

//    init {
//        if (realTime) {
//            Kambridge.hookKeybindUpdatesRealtime(this) {
//                update(isPressed)
//            }
//        } else {
//            Kambridge.hookKeybindUpdates(this) {
//                update(isPressed)
//            }
//        }
//    }

    init {
        if (realTime) {
            WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
                update(isPressed)
            })
        } else {
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
                update(isPressed)
            })
        }
    }

}