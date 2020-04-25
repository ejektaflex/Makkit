package ejektaflex.testmod.mixin

import com.mojang.blaze3d.systems.RenderSystem
import ejektaflex.testmod.RenderHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*


@Mixin(WorldRenderer::class)
abstract class WorldRendererMixin {

    @Accessor("bufferBuilders")
    abstract fun getBufferBuilders(): BufferBuilderStorage

    //@Shadow
    //private val capturedFrustum: Frustum? = null

    private val mc = MinecraftClient.getInstance()

    private fun makeBox(view: Vec3d, pos: Vec3d, size: Vec3d): Box {
        return Box(
                pos.x, pos.y, pos.z,
                pos.x + size.x, pos.y + size.y, pos.z + size.z
        ).offset(pos.x - view.x, pos.y - view.y, pos.z - view.z)
    }



    @Inject(method = ["render"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V", ordinal = 0)])
    private fun render(
            matrices: MatrixStack, tickDelta: Float, limitTime: Long, renderBlockOutline: Boolean,
            camera: Camera, gameRenderer: GameRenderer, lightmapTextureManager: LightmapTextureManager,
            projection: Matrix4f, ci: CallbackInfo
    ) {




        RenderSystem.pushMatrix()

        WorldRenderer.drawBox(
                matrices,
                getBufferBuilders().entityVertexConsumers.getBuffer(RenderHelper.MyRenderLayer.OVERLAY_LINES),
                makeBox(
                        camera.pos,
                        Vec3d(3.0, 3.0, 3.0),
                        Vec3d(2.0, 2.0, 2.0)
                ),
                1f, 1f, 1f, 1f
        )
        //RenderSystem.enableDepthTest()

        RenderSystem.popMatrix()

    }

}