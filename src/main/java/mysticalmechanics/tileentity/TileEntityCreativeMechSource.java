package mysticalmechanics.tileentity;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCreativeMechSource extends TileEntity implements ITickable {
    int ticksExisted = 0;
    private double[] wantedPower = new double[]{10,20,40,80,160,320};
    private int wantedPowerIndex = 0;
    public DefaultMechCapability capability = new DefaultMechCapability(){
        @Override
        public void setPower(double value, EnumFacing from) {
            if(from == null)
                super.setPower(value, from);
        }

        @Override
        public void onPowerChange(){
            TileEntityCreativeMechSource source = TileEntityCreativeMechSource.this;
            source.updateNeighbors();
            source.markDirty();
        }
    };


    public TileEntityCreativeMechSource(){
        super();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setDouble("mech_power", capability.power);
        tag.setInteger("level",wantedPowerIndex);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        if (tag.hasKey("mech_power")){
            capability.power = tag.getDouble("mech_power");
        }
        wantedPowerIndex = tag.getInteger("level") % wantedPower.length;
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
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
            return true;
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
        wantedPowerIndex = (wantedPowerIndex+1) % wantedPower.length;
        return true;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        capability.setPower(0f,null);
        updateNeighbors();
    }

    public void updateNeighbors(){
        for (EnumFacing f : EnumFacing.values()){
            TileEntity t = world.getTileEntity(getPos().offset(f));
            if (t != null){
                if (t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())){
                    t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(capability.getPower(f.getOpposite()),f.getOpposite());
                    t.markDirty();
                }
            }
        }
    }

    @Override
    public void update() {
        ticksExisted++;
        double wantedPower = this.wantedPower[wantedPowerIndex];
        if (capability.getPower(null) != wantedPower){
            capability.setPower(wantedPower,null);
            markDirty();
        }
        updateNeighbors();
    }
}

