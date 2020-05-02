package ejektaflex.kalpis.client.keys

import ejektaflex.kalpis.client.mixin.MixinKeybindDefaultChanger
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil

object KeyRemapper {

    private val remaps = mutableMapOf<String, InputUtil.KeyCode>()

    fun remap(keyId: String, newType: InputUtil.Type, newCode: Int) {
        remaps[keyId] = newType.createFromCode(newCode)
    }

    fun process(binds: Array<KeyBinding>) {
        for (bind in binds) {
            if (bind.id in remaps) {
                (bind as MixinKeybindDefaultChanger).setDefaultKeyCode(remaps[bind.id])
            }
        }
    }

}