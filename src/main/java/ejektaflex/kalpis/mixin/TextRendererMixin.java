package ejektaflex.kalpis.mixin;

import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextRenderer.class)
public interface TextRendererMixin {

    @Accessor
    int getFontHeight();

}
