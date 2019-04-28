package mysticalmechanics.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class DefaultMechCapability implements IMechCapability {
    public double power = 0;

    @Override
    public double getPower(EnumFacing from) {
        return power;
    }

    @Override
    public void setPower(double value, EnumFacing from) {
        double oldPower = power;
        this.power = value;
        if (oldPower != value) {
            onPowerChange();
        }
    }

    @Override
    public void onPowerChange() {

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        power = tag.getDouble("mech_power");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setDouble("mech_power",power);
    }
}
