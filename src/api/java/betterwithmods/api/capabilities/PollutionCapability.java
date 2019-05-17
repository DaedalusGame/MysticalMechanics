package betterwithmods.api.capabilities;

import betterwithmods.api.tile.IPollutant;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PollutionCapability {
    @CapabilityInject(IPollutant.class)
    public static Capability<IPollutant> POLLUTION = null;

    public static class Impl implements Capability.IStorage<IPollutant> {
        @Override
        public NBTBase writeNBT(Capability<IPollutant> capability, IPollutant pollutant, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IPollutant> capability, IPollutant pollutant, EnumFacing side, NBTBase nbt) {

        }
    }

    public static class Default implements IPollutant {
        @Override
        public boolean isPolluting() {
            return false;
        }

        @Override
        public float getPollutionRate() {
            return 0F;
        }
    }
}
