package crimsonfluff.augmentedworkbenches.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceMixin {
    @Shadow private int burnTime;

    @Inject(method = "tick", at = @At("TAIL"))
    protected void onFurnaceTick(CallbackInfo ci){
        AbstractFurnaceBlockEntity furnaceBlockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        World world = furnaceBlockEntity.getWorld();
        BlockPos pos = furnaceBlockEntity.getPos().down();
        if(furnaceBlockEntity.propertyDelegate.get(burnTime) % 20 == 0){
            if( world.getBlockState(pos).getBlock() instanceof FluidBlock && world.getBlockState(pos).getBlock() == Blocks.LAVA){
                FluidState fluidState = world.getFluidState(pos.down());
                furnaceBlockEntity.propertyDelegate.set(burnTime,furnaceBlockEntity.propertyDelegate.get(burnTime) - (int) fluidState.getFluid().getHeight(fluidState,world,pos));
            }
        }

    }
}
