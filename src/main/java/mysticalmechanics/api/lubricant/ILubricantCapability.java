package mysticalmechanics.api.lubricant;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public interface ILubricantCapability {
    int lubricate(ILubricant lubricant, int amount, boolean simulate);

    Collection<LubricantStack> getAppliedLubricant();

    int getCapacity();

    default double getSpeedMod() {
        double speedMod = 1;
        for (LubricantStack stack : getAppliedLubricant()) {
            speedMod *= stack.getLubricant().getSpeedMod();
        }
        return speedMod;
    }

    default double getFrictionMod() {
        double frictionMod = 1;
        for (LubricantStack stack : getAppliedLubricant()) {
            frictionMod *= stack.getLubricant().getFrictionMod();
        }
        return frictionMod;
    }

    default double getHeatMod() {
        double heatMod = 1;
        for (LubricantStack stack : getAppliedLubricant()) {
            heatMod *= stack.getLubricant().getHeatMod();
        }
        return heatMod;
    }

    default NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    default void readFromNBT(NBTTagCompound tag) {}
}
