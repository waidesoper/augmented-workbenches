package crimsonfluff.augmentedworkbenches.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceMixin {


    @Inject(method = "tick", at = @At("HEAD"))
    protected void onFurnaceTick(CallbackInfo ci){
        AbstractFurnaceBlockEntity furnaceBlockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        World world = furnaceBlockEntity.getWorld();
        List<BlockPos> posList = new ArrayList<>();
        posList.add(furnaceBlockEntity.getPos().down());
        posList.add(furnaceBlockEntity.getPos().up());
        posList.add(furnaceBlockEntity.getPos().north());
        posList.add(furnaceBlockEntity.getPos().south());
        posList.add(furnaceBlockEntity.getPos().east());
        posList.add(furnaceBlockEntity.getPos().west());

        if(!world.isClient) {
            for(BlockPos pos : posList) {
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (block instanceof FluidBlock) {
                    FluidState fluidState = world.getFluidState(pos);
                    int modifier = fluidState.getFluid().getLevel(fluidState);
                    if (block == Blocks.LAVA) { //a full block smelts at 8 times speed, but consumes fuel at the same rate (8 items, 1 coal, just over a second)
                        furnaceBlockEntity.cookTime = MathHelper.clamp(furnaceBlockEntity.cookTime + modifier - 1, 0, furnaceBlockEntity.cookTimeTotal - 1);
                        furnaceBlockEntity.burnTime = MathHelper.clamp(furnaceBlockEntity.burnTime + 1 - modifier, 0, furnaceBlockEntity.fuelTime);
                    }
                } else if (block == Blocks.MAGMA_BLOCK) {//smelts at double speed fuel is consumed at half rate (smelt 16 items with 1 coal in 10 seconds in a regular furnace)
                    furnaceBlockEntity.cookTime = MathHelper.clamp(furnaceBlockEntity.cookTime + 1, 0, furnaceBlockEntity.cookTimeTotal - 1);
                }
                world.markDirty(pos, furnaceBlockEntity);
            }
        }
    }
}
