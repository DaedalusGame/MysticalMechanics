package betterwithmods.api.util;

import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IMechanicalUtil {

    IMechanicalPower getMechanicalPower(World world, BlockPos pos, EnumFacing facing);

    IAxle getAxle(IBlockAccess world, BlockPos pos, EnumFacing facing);

    boolean isRedstonePowered(World world, BlockPos pos);

    boolean canInput(World world, BlockPos pos, EnumFacing facing);

    boolean isAxle(IBlockAccess world, BlockPos pos, EnumFacing facing);

    int getPowerOutput(World world, BlockPos pos, EnumFacing facing);
}
