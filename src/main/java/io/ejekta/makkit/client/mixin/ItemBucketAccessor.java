package io.ejekta.makkit.client.mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/*
    Used to grab the block from a bucket, for various fill operations
 */
@Mixin(BucketItem.class)
public interface ItemBucketAccessor {

    @Accessor
    Fluid getFluid();

}
