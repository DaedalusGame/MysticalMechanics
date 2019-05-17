package betterwithmods.api.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface ISteamPower {
    void readSteamPower(NBTTagCompound tag);

    NBTTagCompound writeSteamPower(NBTTagCompound tag);

    int getHeatUnits(EnumFacing facing);

    void calculateHeatUnits();

    int getSteamPower(EnumFacing facing);

    void calculateSteamPower(@Nullable EnumFacing facing);

    void setSteamUpdate(boolean update);

    boolean canTransferItem();
}
