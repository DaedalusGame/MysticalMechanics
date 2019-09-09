package mysticalmechanics.block;

import mysticalmechanics.tileentity.TileEntityGearbox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockGearbox extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    public BlockGearbox(Material material) {
        super(material);
    }

    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(facing).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(facing, EnumFacing.getFront(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        if(placer.isSneaking())
            facing = facing.getOpposite();
        return getDefaultState().withProperty(BlockGearbox.facing, facing);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
        return side != state.getValue(facing);
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
        return new TileEntityGearbox();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        TileEntityGearbox tile = (TileEntityGearbox)world.getTileEntity(pos);
        tile.shouldUpdate = true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityGearbox tile = (TileEntityGearbox)world.getTileEntity(pos);
        return tile.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityGearbox tile = (TileEntityGearbox)world.getTileEntity(pos);
        tile.breakBlock(world,pos,state,player);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
        TileEntityGearbox tile = (TileEntityGearbox)world.getTileEntity(pos);
        tile.rotateTile(world, pos, side);
        return true;
    }
}

