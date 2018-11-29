package mysticalmechanics.tileentity;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IAxle;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockAxle;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityAxle extends TileEntity implements ITickable, IAxle {
    BlockPos left;
    BlockPos right;
    public double angle, lastAngle;
    public DefaultMechCapability capability = new DefaultMechCapability(){
        @Override
        public void onPowerChange(){
            TileEntityAxle axle = TileEntityAxle.this;
            axle.updatePower();
            axle.markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
            if(from == null) {
                return super.getPower(null);
            }
            AxisDirection otherDirection = from.getOpposite().getAxisDirection();
            if(TileEntityAxle.this.isConnection(otherDirection))
                return super.getPower(from);
            else {
                TileEntityAxle otherAxle = TileEntityAxle.this.getConnectionTile(otherDirection);
                if(otherAxle != null)
                    return otherAxle.capability.getPower(null);
            }
            return 0;
        }
    };

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        if(left == null)
            left = posIn;
        if(right == null)
            right = posIn;
    }

    public void updatePower() {
        TileEntityAxle leftAxle = getConnectionTile(AxisDirection.NEGATIVE);
        TileEntityAxle rightAxle = getConnectionTile(AxisDirection.POSITIVE);

            EnumFacing leftFacing = EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, getAxis());
            EnumFacing rightFacing = EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, getAxis());
            TileEntity leftTile = world.getTileEntity(getConnection(AxisDirection.NEGATIVE).offset(leftFacing));
            TileEntity rightTile = world.getTileEntity(getConnection(AxisDirection.POSITIVE).offset(rightFacing));
            if(rightAxle != null && leftTile != null && !(leftTile instanceof TileEntityAxle) && leftTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, leftFacing.getOpposite()))
                leftTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, leftFacing.getOpposite()).setPower(rightAxle.capability.getPower(null),leftFacing.getOpposite());
            if(leftAxle != null && rightTile != null && !(rightTile instanceof TileEntityAxle) && rightTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, rightFacing.getOpposite()))
                rightTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, rightFacing.getOpposite()).setPower(leftAxle.capability.getPower(null),rightFacing.getOpposite());
    }

    public Axis getAxis() {
        IBlockState state = world.getBlockState(getPos());
        return state.getValue(BlockAxle.axis);
    }

    public void updateConnection(){
        updateConnectionSided(AxisDirection.POSITIVE);
        updateConnectionSided(AxisDirection.NEGATIVE);
        updatePower();
    }

    public void updateConnectionSided(AxisDirection dir){
        EnumFacing checkFacing = EnumFacing.getFacingFromAxis(dir, getAxis());
        BlockPos newSide = checkAndReturnConnection(pos.offset(checkFacing),dir);
        if(newSide != null && !newSide.equals(getConnection(dir))) {
            setConnection(dir,newSide);
            TileEntity neighborTile = world.getTileEntity(pos.offset(checkFacing.getOpposite()));
            if (neighborTile instanceof TileEntityAxle)
                ((TileEntityAxle) neighborTile).updateConnectionSided(dir);
        }
    }

    public BlockPos checkAndReturnConnection(BlockPos checkPos, AxisDirection dir)
    {
        if(!world.isBlockLoaded(checkPos))
            return null;
        TileEntity checkTile = world.getTileEntity(checkPos);
        if(checkTile instanceof TileEntityAxle && ((TileEntityAxle) checkTile).getAxis() == getAxis())
            return ((TileEntityAxle) checkTile).getConnection(dir);
        else
            return getPos();
    }

    public BlockPos getConnection(AxisDirection dir)
    {
        return dir == AxisDirection.POSITIVE ? right : left;
    }

    public void setConnection(AxisDirection dir, BlockPos pos)
    {
        if (dir == AxisDirection.POSITIVE)
            right = pos;
        else
            left = pos;
        markDirty();
    }

    public boolean isConnection(AxisDirection dir) {
        return getConnection(dir).equals(pos);
    }

    public TileEntityAxle getConnectionTile(AxisDirection dir) {
        TileEntity tile = world.getTileEntity(getConnection(dir));
        if(tile instanceof TileEntityAxle)
            return (TileEntityAxle) tile;
        return null;
    }

    public void updateNeighbors(){
        updateConnection();
    }

    public TileEntityAxle(){
        super();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setDouble("mech_power", capability.power);
        tag.setInteger("left_x",left.getX());
        tag.setInteger("left_y",left.getY());
        tag.setInteger("left_z",left.getZ());
        tag.setInteger("right_x",right.getX());
        tag.setInteger("right_y",right.getY());
        tag.setInteger("right_z",right.getZ());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        capability.power = tag.getDouble("mech_power");
        left = new BlockPos(tag.getInteger("left_x"),tag.getInteger("left_y"),tag.getInteger("left_z"));
        right = new BlockPos(tag.getInteger("right_x"),tag.getInteger("right_y"),tag.getInteger("right_z"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
            IBlockState state = world.getBlockState(getPos());
            if (state.getBlock() instanceof BlockAxle){
                EnumFacing.Axis axis = state.getValue(BlockAxle.axis);
                if (facing != null && axis == facing.getAxis()) {
                    return true;
                }
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
            return (T)this.capability;
        }
        return super.getCapability(capability, facing);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        capability.setPower(0f,null);
    }

    @Override
    public void update() {
        if(world.isRemote) {
            lastAngle = angle;
            angle += capability.getPower(null);
        }
    }
}
