package mysticalmechanics.api;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public interface IHasRotation {
    boolean hasRotation(@Nonnull EnumFacing side);

    double getAngle(@Nonnull EnumFacing side);

    double getLastAngle(@Nonnull EnumFacing side);
}
