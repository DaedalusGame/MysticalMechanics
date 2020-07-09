package mysticalmechanics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface IGearBehavior {
    IGearBehavior NO_BEHAVIOR = new IGearBehavior() {
        @Override
        public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
            return power;
        }


        @Override
        public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data) {
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
    default double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        return transformPower(tile, facing, gear, null, power);
    }

    /**
     * Works like {@link #transformPower(TileEntity, EnumFacing, ItemStack, double)}, but also accepts gear data
     *
     * @param data the gear's data container
     */
    default double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
        return transformPower(tile,facing,gear,power);
    }

    /**
     * Implement to modify how fast the gear spins when rendered.
     *
     * @param tile the TileEntity this gear is attached to
     * @param facing which face the gear is attached to
     * @param gear the ItemStack representing the attached gear
     * @param power how much power would be returned at full efficiency (100%)
     * @return how much power will actually be returned.
     */
    default double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        return transformPower(tile,facing,gear,power);
    }

    /**
     * Works like {@link #transformVisualPower(TileEntity, EnumFacing, ItemStack, double)}, but also accepts gear data
     *
     * @param data the gear's data container
     */
    default double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
        return transformVisualPower(tile, facing, gear, power);
    }

    /**
     * Implement this to provide special visual effects while the gear is attached to a gearbox or machine.
     *
     * @param tile the TileEntity this gear is attached to
     * @param facing which face the gear is attached to
     * @param gear the ItemStack representing the attached gear
     */
    default void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
        visualUpdate(tile, facing, gear, null);
    }

    /**
     * Works like {@link #visualUpdate(TileEntity, EnumFacing, ItemStack)}, but also accepts gear data
     *
     * @param data the gear's data container
     */
    default void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data) {
        //NOOP
    }

    default boolean canTick(ItemStack gear) {
        return false;
    }

    default void tick(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        tick(tile, facing, gear, null, power);
    }

    default void tick(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
        //NOOP;
    }

    default boolean hasData() {
        return false;
    }

    default IGearData createData() {
        return null;
    }
}
