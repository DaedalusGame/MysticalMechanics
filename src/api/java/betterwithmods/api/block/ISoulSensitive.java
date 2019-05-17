package betterwithmods.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ISoulSensitive {
    /**
     * In case only certain blocks can consume souls. Return true if all of them can.
     */
    boolean isSoulSensitive(IBlockAccess world, BlockPos pos);

    /**
     * How many souls your block can consume simultaneously.
     */
    int getMaximumSoulIntake(IBlockAccess world, BlockPos pos);

    /**
     * Similar to RF transfer, you will want to return the lowest number between
     * maximum transfer and the number of souls available.
     */
    int processSouls(World world, BlockPos pos, int souls);

    /**
     * Tells the filtered hopper if it can consume the souls.
     */
    boolean consumeSouls(World world, BlockPos pos, int souls);
}
