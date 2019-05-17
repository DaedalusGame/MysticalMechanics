package betterwithmods.api.tile;

import net.minecraft.util.EnumFacing;

public interface IAxle extends IMechanicalPower {
    byte getSignal();

    byte getMaximumSignal();

    int getMaximumInput();

    int getMinimumInput();

    EnumFacing[] getDirections();

    EnumFacing.Axis getAxis();

    default boolean isFacing(IAxle axle) {
        return axle.getAxis() == this.getAxis();
    }

}
