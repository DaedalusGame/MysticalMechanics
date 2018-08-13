package mysticalmechanics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface IGearBehavior {
    IGearBehavior NO_BEHAVIOR = new IGearBehavior() {
        @Override
        public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
            return power;
        }


        @Override
        public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
            //NOOP;
        }
    };

    /**
     * Implement to modify how much power this gear can transmit. This should be suitable to implement all manner of mechanics like low/high-pass filters, friction and power limits.
     *
     * @param tile the TileEntity this gear is attached to
     * @param facing which face the gear is attached to
     * @param gear the ItemStack representing the attached gear
     * @param power how much power would be returned at full efficiency (100%) (note: for the standard gearbox the division by the number of connections for output gears is already included here)
     * @return how much power will actually be returned. Should absolutely NEVER be greater than the power passed into this method, or the gear can lead to power loops!
     */
    double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power);

    /**
     * Implement this to provide special visual effects while the gear is attached to a gearbox or machine.
     *
     * @param tile the TileEntity this gear is attached to
     * @param facing which face the gear is attached to
     * @param gear the ItemStack representing the attached gear
     */
    void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear);
}
