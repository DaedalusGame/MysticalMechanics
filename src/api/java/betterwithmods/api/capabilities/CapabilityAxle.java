package betterwithmods.api.capabilities;

import betterwithmods.api.tile.IAxle;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityAxle {
    @SuppressWarnings("CanBeFinal")
    @CapabilityInject(IAxle.class)
    public static Capability<IAxle> AXLE = null;

    public static class Impl implements Capability.IStorage<IAxle> {
        @Override
        public NBTBase writeNBT(Capability<IAxle> capability, IAxle mechanical, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IAxle> capability, IAxle mechanical, EnumFacing side, NBTBase nbt) {

        }
    }

    public static class Default implements IAxle {

        @Override
        public byte getSignal() {
            return 0;
        }

        @Override
        public byte getMaximumSignal() {
            return 0;
        }

        @Override
        public int getMaximumInput() {
            return 0;
        }

        @Override
        public int getMinimumInput() {
            return 0;
        }

        @Override
        public EnumFacing[] getDirections() {
            return new EnumFacing[0];
        }

        @Override
        public EnumFacing.Axis getAxis() {
            return null;
        }

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
