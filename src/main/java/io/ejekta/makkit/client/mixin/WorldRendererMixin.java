package io.ejekta.makkit.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.ejekta.makkit.client.event.Events;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Accessor
    abstract BufferBuilderStorage getBufferBuilders();

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V",
            ordinal = 0
    ))
    private void render(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f, Matrix4f matrix4f2,
            CallbackInfo ci,
            @Local MatrixStack matrixStack
    ) {
        Events.RenderWorldEvent.Companion.getDispatcher().invoker().invoke(
                new Events.RenderWorldEvent(matrixStack, tickCounter, camera, gameRenderer, getBufferBuilders(), matrix4f)
        );
    }

}
