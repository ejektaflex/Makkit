package ejektaflex.testmod.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


@Mixin(WorldRenderer::class)
abstract class WorldRendererMixin {

    @Accessor("bufferBuilders")
    abstract fun getBufferBuilders(): BufferBuilderStorage

    //@Shadow
    //private val capturedFrustum: Frustum? = null

    @Inject(method = ["render"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V", ordinal = 0)])
    private fun render(
            matrices: MatrixStack, tickDelta: Float, limitTime: Long, renderBlockOutline: Boolean,
            camera: Camera, gameRenderer: GameRenderer, lightmapTextureManager: LightmapTextureManager,
            projection: Matrix4f, ci: CallbackInfo
    ) {
        val immediate = MinecraftClient.getInstance().gameRenderer
        WorldRenderer.drawBox(
                matrices,
                getBufferBuilders().entityVertexConsumers.getBuffer(RenderLayer.getLines()),
                Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                1f, 1f, 1f, 1f
        )
    }

}