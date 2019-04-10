package mysticalmechanics.block;


import mysticalmechanics.tileentity.TileEntityAxle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAxle extends Block {
	
	public static final PropertyDirection facing = PropertyDirection.create("facing"); 
	
    public BlockAxle(Material material) {
        super(material);
    }

    @Override
    public BlockStateContainer createBlockState() {
    	return new BlockStateContainer(this, facing);    	        
    }

    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(facing).ordinal();       
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
    	return getDefaultState().withProperty(facing, EnumFacing.getFront(meta));        
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    	return getDefaultState().withProperty(facing, face);       
    }   

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {    	
        return side.getAxis() != state.getValue(facing).getAxis();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityAxle();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        TileEntityAxle p = (TileEntityAxle) world.getTileEntity(pos);
        p.neighborChanged(fromPos);        
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {        
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos,this,0);       
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityAxle p = (TileEntityAxle)world.getTileEntity(pos);
        return p.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityAxle p = (TileEntityAxle)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(facing).getAxis()) {
            case Y:
                return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1.0, 0.625);
            case Z:
                return new AxisAlignedBB(0.375, 0.375, 0, 0.625, 0.625, 1.0);
            case X:
                return new AxisAlignedBB(0, 0.375, 0.375, 1.0, 0.625, 0.625);
        }
        return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1.0, 0.625);
    }
}
