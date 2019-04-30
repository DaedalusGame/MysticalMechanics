package mysticalmechanics.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IAxle {
    default BlockPos getLeftConnection() {
        return getConnection(EnumFacing.AxisDirection.NEGATIVE);
    }

    default BlockPos getRightConnection() {
        return getConnection(EnumFacing.AxisDirection.POSITIVE);
    }

    /**
     * Use/implement to retrieve the leftmost and rightmost axle. Connectivity of axles should atleast provide that much.
     *
     * @param direction either positive or negative axis direction.
     * @return the position of the last segment of axle in that direction.
     */
    BlockPos getConnection(EnumFacing.AxisDirection direction);

    /**
     * Use this to obtain the length of the axle.
     *
     * @return the length between the two outermost segments of an axle
     */
    default int getLength() {
        BlockPos left = getLeftConnection();
        BlockPos right = getRightConnection();

        int dx = Math.abs(left.getX() - right.getX());
        int dy = Math.abs(left.getY() - right.getY());
        int dz = Math.abs(left.getZ() - right.getZ());

        return Math.max(Math.max(dx, dy), dz) - 1; //Note that by default axles don't bend, so their distance is simply dx, dy or dz, whichever is highest
    }
}
