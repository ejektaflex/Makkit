package ejektaflex.kalpis.mixin

import ejektaflex.kalpis.event.Events
import net.minecraft.client.render.*
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.client.util.math.MatrixStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


@Mixin(WorldRenderer::class)
abstract class WorldRendererMixin {

    @Accessor("bufferBuilders")
    abstract fun getBufferBuilders(): BufferBuilderStorage


    @Inject(method = ["render"],
            at = [At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V", ordinal = 0)])
    private fun render(
            matrices: MatrixStack, tickDelta: Float, limitTime: Long, renderBlockOutline: Boolean,
            camera: Camera, gameRenderer: GameRenderer, lightmapTextureManager: LightmapTextureManager,
            projection: Matrix4f, ci: CallbackInfo
    ) {
        Events.DrawScreenEvent.Dispatcher.invoker()(Events.DrawScreenEvent(matrices, tickDelta, camera, gameRenderer, getBufferBuilders()))
    }

}