package crimsonfluff.augmentedworkbenches.mixin;

import crimsonfluff.augmentedworkbenches.AugmentedWorkbenches;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceMixin {


    @Inject(method = "tick", at = @At("HEAD"))
    protected void onFurnaceTick(CallbackInfo ci){
        AbstractFurnaceBlockEntity furnaceBlockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        World world = furnaceBlockEntity.getWorld();
        BlockPos pos = furnaceBlockEntity.getPos().down();

            if(!world.isClient) {
                if (world.getBlockState(pos).getBlock() instanceof FluidBlock && world.getBlockState(pos).getBlock() == Blocks.LAVA) {
                    FluidState fluidState = world.getFluidState(pos);
                    int modifier = fluidState.getFluid().getLevel(fluidState);
                    furnaceBlockEntity.cookTime = MathHelper.clamp(furnaceBlockEntity.cookTime + modifier, 0, furnaceBlockEntity.cookTimeTotal -1);
                    AugmentedWorkbenches.LOGGER.info(fluidState.getFluid().getLevel(fluidState));
                    world.markDirty(pos, furnaceBlockEntity);
                }
            }

    }
}
