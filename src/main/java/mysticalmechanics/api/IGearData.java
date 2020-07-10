package mysticalmechanics.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IGearData {
    void readFromNBT(NBTTagCompound tag);

    NBTTagCompound writeToNBT(NBTTagCompound tag);
}
