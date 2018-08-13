package mysticalmechanics.api;

import net.minecraft.util.EnumFacing;

public interface IMechCapability {
    /**
     * Use to retrieve how much power is provided from this block on a certain face.
     * Expected behavior is that this method returns 0 if the face is not an output face.
     *
     * @param from the face from which power is retrieved. Use null for internal use.
     * @return how much power is provided from the specified face.
     */
    double getPower(EnumFacing from);

    /**
     * Use to provide power to a block/entity from a certain face.
     * Expected behavior is that this method does nothing if the face is not an input face.
     *
     * @param value how much power is provided. Unitless.
     * @param from the face from which power is provided. Use null for internal use.
     */
    void setPower(double value, EnumFacing from);

    /**
     * This method should be run when setPower changes the internal power to something other than what it was before.
     * Expected behavior is that this method is used to propagate power change through the network.
     */
    void onPowerChange();
}
