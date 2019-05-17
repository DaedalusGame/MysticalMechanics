package betterwithmods.api.tile.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public interface IExternalCapability {
    boolean hasExternalCapability(BlockPos pos, Capability<?> capability, @Nullable EnumFacing facing);

    <T> T getExternalCapability(BlockPos pos, Capability<?> capability, @Nullable EnumFacing facing);
}
