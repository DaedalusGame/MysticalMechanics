package betterwithmods.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IRenderRotationPlacement {
    IBlockState getRenderState(World world, BlockPos pos, EnumFacing facing, float flX, float flY, float flZ, int meta, EntityLivingBase placer);
    RenderFunction getRenderFunction();
    interface RenderFunction {
        void render(World world, Block block, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing side, RayTraceResult target, double partial);
    }
}
