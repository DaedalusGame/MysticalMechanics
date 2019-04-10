package mysticalmechanics.block;

import mysticalmechanics.tileentity.TileEntityCreativeMechSource;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCreativeMechSource extends Block {
    public BlockCreativeMechSource() {
        super(Material.IRON);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCreativeMechSource();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityCreativeMechSource p = (TileEntityCreativeMechSource)world.getTileEntity(pos);
        return p.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        TileEntityCreativeMechSource p = (TileEntityCreativeMechSource)world.getTileEntity(pos);
        p.updateNeighbors();        
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile != null && tile instanceof TileEntityCreativeMechSource) {
    		((TileEntityCreativeMechSource)tile).updateNeighbors();
    	}
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityCreativeMechSource p = (TileEntityCreativeMechSource)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }
}

