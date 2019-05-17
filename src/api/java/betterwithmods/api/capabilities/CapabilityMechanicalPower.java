package betterwithmods.api.capabilities;

import betterwithmods.api.tile.IMechanicalPower;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityMechanicalPower {
    @SuppressWarnings("CanBeFinal")
    @CapabilityInject(IMechanicalPower.class)
    public static Capability<IMechanicalPower> MECHANICAL_POWER = null;

    public static class Impl implements Capability.IStorage<IMechanicalPower> {
        @Override
        public NBTBase writeNBT(Capability<IMechanicalPower> capability, IMechanicalPower mechanical, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IMechanicalPower> capability, IMechanicalPower mechanical, EnumFacing side, NBTBase nbt) {

        }
    }

    public static class Default implements IMechanicalPower {
        @Override
        public int getMechanicalOutput(EnumFacing facing) {
            return 0;
        }

        @Override
        public int getMechanicalInput(EnumFacing facing) {
            return 0;
        }

        @Override
        public int getMaximumInput(EnumFacing facing) {
            return 0;
        }

        @Override
        public int getMinimumInput(EnumFacing facing) {
            return 0;
        }

        @Override
        public Block getBlock() {
            return null;
        }

        @Override
        public World getBlockWorld() {
            return null;
        }

        @Override
        public BlockPos getBlockPos() {
            return null;
        }

    }
}
