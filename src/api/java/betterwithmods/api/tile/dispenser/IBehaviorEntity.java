package betterwithmods.api.tile.dispenser;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 5/25/17.
 */
@FunctionalInterface
public interface IBehaviorEntity {
    NonNullList<ItemStack> collect(World world, BlockPos pos, Entity entity, ItemStack stack);
}
