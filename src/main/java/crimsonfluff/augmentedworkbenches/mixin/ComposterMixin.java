package crimsonfluff.augmentedworkbenches.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ComposterBlock.class)
public class ComposterMixin {

    @Inject(method = "onUse", at = @At("HEAD"))
    public void onComposterUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable cir) {
        List<BlockPos> posList = new ArrayList<>();
        BlockPos.stream(new BlockPos(pos.getX() - 3, pos.getY(), pos.getZ() - 3),
                new BlockPos(pos.getX() + 3, pos.getY(), pos.getZ() + 3))
                .forEach(block -> {
                    if(world.getBlockState(block).getBlock() instanceof ComposterBlock) {
                        posList.add(new BlockPos(block));
                    }
                });
        posList.remove(pos);
        for (BlockPos composterPos : posList){
            BlockState state1 = world.getBlockState(composterPos);
            int i = state1.get(ComposterBlock.LEVEL);
            ItemStack itemStack = player.getStackInHand(hand);
            if (i < 8 && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
                if (i < 7 && !world.isClient) {
                    BlockState blockState = ComposterBlock.addToComposter(state1, world, composterPos, itemStack);
                    world.syncWorldEvent(1500, composterPos, state1 != blockState ? 1 : 0);
                    if (!player.abilities.creativeMode) {
                        itemStack.decrement(1);
                    }
                }
            }
            if (i == 8) {
                ComposterBlock.emptyFullComposter(state1, world, composterPos);
            }
        }

    }
}
