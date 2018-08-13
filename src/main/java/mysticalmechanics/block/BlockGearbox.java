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
        return getDefaultState().withProperty(facing, EnumFacing.getDirectionFromEntityLiving(pos, placer));
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
        TileEntityGearbox p = (TileEntityGearbox)world.getTileEntity(pos);
        p.updateNeighbors();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityGearbox p = (TileEntityGearbox)world.getTileEntity(pos);
        return p.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityGearbox p = (TileEntityGearbox)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }
}

