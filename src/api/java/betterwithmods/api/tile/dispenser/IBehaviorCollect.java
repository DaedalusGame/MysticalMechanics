package betterwithmods.api.tile.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 5/25/17.
 */
@FunctionalInterface
public interface IBehaviorCollect {
    NonNullList<ItemStack> collect(IBlockSource source);

    default void breakBlock(World world, IBlockState state, BlockPos pos) {
        world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, null).getPlaceSound(), SoundCategory.BLOCKS, 0.7F, 1.0F);
        state.getBlock().breakBlock(world, pos, state);
        world.playEvent(2001, pos, Block.getIdFromBlock(state.getBlock()));
        world.setBlockToAir(pos);
    }
}
