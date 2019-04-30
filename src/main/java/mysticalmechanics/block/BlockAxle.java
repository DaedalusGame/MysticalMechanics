package mysticalmechanics.block;


import mysticalmechanics.tileentity.TileEntityAxle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAxle extends Block {
	
	//public static final PropertyDirection facing = PropertyDirection.create("facing");
    public static final PropertyEnum<EnumFacing.Axis> axis = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class);
	
    public BlockAxle(Material material) {
        super(material);
    }

    @Override
    public BlockStateContainer createBlockState() {
    	return new BlockStateContainer(this, axis);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(axis).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis[] axisSet = EnumFacing.Axis.values();
        return getDefaultState().withProperty(axis, axisSet[meta % axisSet.length]);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    	return getDefaultState().withProperty(axis, face.getAxis());
    }   

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {    	
        return side.getAxis() != state.getValue(axis);
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
        TileEntityAxle tile = (TileEntityAxle) world.getTileEntity(pos);
        tile.neighborChanged(fromPos);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {        
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos,this,0);       
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    	//TileEntityAxle tile = (TileEntityAxle) world.getTileEntity(pos);
    	//tile.setConnection();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityAxle tile = (TileEntityAxle)world.getTileEntity(pos);
        return tile.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityAxle tile = (TileEntityAxle)world.getTileEntity(pos);
        tile.breakBlock(world,pos,state,player);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(axis)) {
            case Y:
                return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1.0, 0.625);
            case Z:
                return new AxisAlignedBB(0.375, 0.375, 0, 0.625, 0.625, 1.0);
            case X:
                return new AxisAlignedBB(0, 0.375, 0.375, 1.0, 0.625, 0.625);
        }
        return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1.0, 0.625);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing.Axis currentAxis = state.getValue(axis);
        TileEntityAxle tile = (TileEntityAxle) world.getTileEntity(pos);

        if(side.getAxis() == currentAxis) {
            return false;
        }

        tile.rotateTile(world, pos, side);

        return true;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        EnumFacing.Axis currentAxis = state.getValue(axis);
        currentAxis = rot.rotate(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE,currentAxis)).getAxis();
        return state.withProperty(axis,currentAxis);
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        EnumFacing.Axis currentAxis = state.getValue(axis);
        currentAxis = mirrorIn.mirror(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE,currentAxis)).getAxis();
        return state.withProperty(axis,currentAxis);
    }

    /*@Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tile = world.getTileEntity(data.getPos());
        if(tile instanceof TileEntityAxle)
            ((TileEntityAxle) tile).addProbeInfo(mode,probeInfo,player,world,blockState,data);
    }*/
}
