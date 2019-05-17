package betterwithmods.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface IAdvancedRotationPlacement {

    IBlockState getStateForAdvancedRotationPlacement(IBlockState defaultState, EnumFacing facing, float hitX, float hitY, float hitZ);



    default boolean isMax(double hit1, double hit2) {
        return Math.max(Math.abs(hit1), Math.abs(hit2)) == Math.abs(hit1);
    }

    default boolean inCenter(float hit1, float hit2, float max) {
        return Math.abs(hit1) <= max && Math.abs(hit2) <= max;
    }

}