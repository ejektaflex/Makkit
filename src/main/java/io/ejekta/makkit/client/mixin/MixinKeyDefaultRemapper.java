package io.ejekta.makkit.client.mixin;

import io.ejekta.makkit.client.editor.input.KeyRemapper;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class MixinKeyDefaultRemapper {
    @Shadow
    public KeyBinding[] keysAll;

    @Inject(at = @At("HEAD"), method = "load()V")
    public void loadHook(CallbackInfo info) {
        KeyRemapper.INSTANCE.process(keysAll);
    }
}
