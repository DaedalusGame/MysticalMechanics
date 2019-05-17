package betterwithmods.api.util;

import net.minecraft.block.state.IBlockState;

public interface IWoodProvider {

    boolean match(IBlockState state);

    IWood getWood(IBlockState state);

}
