package betterwithmods.api.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAxleTick {
    default void tick(World world, BlockPos pos, IAxle axle) {}
}
