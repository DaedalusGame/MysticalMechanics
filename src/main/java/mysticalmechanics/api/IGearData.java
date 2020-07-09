package mysticalmechanics.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IGearData {
    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
