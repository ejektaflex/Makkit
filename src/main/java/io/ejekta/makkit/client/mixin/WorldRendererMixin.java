package io.ejekta.makkit.client.mixin;

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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V", ordinal = 0))
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        Events.DrawScreenEvent.Companion.getDispatcher().invoker().invoke(
                new Events.DrawScreenEvent(matrices, tickDelta, camera, gameRenderer, getBufferBuilders(), positionMatrix)
        );
    }

}
