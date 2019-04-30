package mysticalmechanics.tileentity;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearbox;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockGearbox;
import mysticalmechanics.handler.RegistryHandler;
import mysticalmechanics.util.ISoundController;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TileEntityGearbox extends TileEntity implements ITickable, IGearbox, ISoundController {
    EnumFacing from = null;       
    protected boolean isBroken;
    public int connections = 0;
    public ItemStack[] gears = new ItemStack[]{
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY
    };
    public double[] angles = new double[6];
    public double[] lastAngles = new double[6];

    public DefaultMechCapability capability;

    //Don't look at me
    public static final int SOUND_SLOW_LV1 = 1;
    public static final int SOUND_SLOW_LV2 = 2;
    public static final int SOUND_SLOW_LV3 = 3;
    public static final int SOUND_MID_LV1 = 4;
    public static final int SOUND_MID_LV2 = 5;
    public static final int SOUND_MID_LV3 = 6;
    public static final int SOUND_FAST_LV1 = 7;
    public static final int SOUND_FAST_LV2 = 8;
    public static final int SOUND_FAST_LV3 = 9;
    public static final int[] SOUND_IDS = new int[]{SOUND_SLOW_LV1,SOUND_SLOW_LV2,SOUND_SLOW_LV3,SOUND_MID_LV1,SOUND_MID_LV2,SOUND_MID_LV3,SOUND_FAST_LV1,SOUND_FAST_LV2,SOUND_FAST_LV3};

    HashMap<Integer,Integer> soundsPlaying = new HashMap<>();

    public TileEntityGearbox() {
        super();
        capability = createCapability();
    }

    public DefaultMechCapability createCapability() {
        return new DefaultMechCapability() {
            @Override
            public void onPowerChange() {
                TileEntityGearbox box = TileEntityGearbox.this;
                box.updateNeighbors();
                box.markDirty();
            }

            @Override
            public double getPower(EnumFacing from) {
                ItemStack gearStack = getGear(from);
                if (from != null && gearStack.isEmpty()) {
                    return 0;
                }
                IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
                double unchangedPower = 0;
                
                //need to work out solution for null checks that aren't the renderer.
                if (from == TileEntityGearbox.this.from)//|| from == null
                    unchangedPower = capability.power;
                else
                    unchangedPower = power / ((double) (Math.max(1, getConnections())));

                return behavior.transformPower(TileEntityGearbox.this, from, gearStack, unchangedPower);
            }

            @Override
            public void setPower(double value, EnumFacing from) {            	
            	ItemStack gearStack = getGear(from);
            	if(from == null) {
                    this.power = 0;
                    onPowerChange();
                }
                if (from != null && gearStack.isEmpty()) {
                	if(capability.power != 0) {
                		this.power = 0;
                		onPowerChange();
                	}
                }        
                if (isInput(from) && !gearStack.isEmpty()) {
                    IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
                	double oldPower = capability.power;
                	value = behavior.transformPower(TileEntityGearbox.this,from,gearStack,value);
                    if (oldPower != value) {
                    	capability.power = value;
                        onPowerChange();
                    }          
                }               
            }

            @Override
            public boolean isInput(EnumFacing from) {
            	return TileEntityGearbox.this.from == from;
            }

            @Override
            public boolean isOutput(EnumFacing from) {
                return TileEntityGearbox.this.from != from;
            }
        };
    }

    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        
        //sets Gearbox Input.
        if (state.getBlock() instanceof BlockGearbox) {
            from = state.getValue(BlockGearbox.facing);
            TileEntity t = world.getTileEntity(getPos().offset(from));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()) && capability.isInput(from)) {
            	if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).isOutput(from.getOpposite())&& !getGear(from).isEmpty()) {
            		capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).getPower(from.getOpposite()), from);
            	}else if(getGear(from).isEmpty()&&capability.isInput(from)) {
            		capability.setPower(0, from);
            	 }
            }               
        }
        
        connections = 0;
        List<EnumFacing> toUpdate = new ArrayList<>();
        for (EnumFacing f : EnumFacing.values()) {
            if (f != null && f != from) {
                TileEntity t = world.getTileEntity(getPos().offset(f));
                if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())) {                	
                    if (!getGear(f).isEmpty() && capability.getPower(f) != 0) {
                    	connections++;
                    }
                    toUpdate.add(f);
                    
                    
                }
            }                  
        }
        
        //Manages Power Output.
        for (EnumFacing f : toUpdate) {
            BlockPos p = getPos().offset(f);
            TileEntity t = world.getTileEntity(p);            
            if(t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()) && capability.isOutput(f)) {
            	if(!getGear(f).isEmpty()) {            		
            		t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(capability.getPower(f), f.getOpposite());
            	}else if (getGear(f).isEmpty()) {
            		t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(0, f.getOpposite());
            	}
            }            	
        }
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        capability.writeToNBT(tag);
        if (from != null) {
            tag.setInteger("from", from.getIndex());
        }
        for (int i = 0; i < 6; i++) {
            tag.setTag("gear" + i, gears[i].writeToNBT(new NBTTagCompound()));
        }
        tag.setInteger("connections", connections);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        capability.readFromNBT(tag);
        if (tag.hasKey("from")) {
            from = EnumFacing.getFront(tag.getInteger("from"));
        }
        for (int i = 0; i < 6; i++) {
            gears[i] = new ItemStack(tag.getCompoundTag("gear" + i));
        }
        connections = tag.getInteger("connections");
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
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY) {
        	@SuppressWarnings("unchecked") 
			T result = (T) this.capability;
            return result;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void attachGear(EnumFacing facing, ItemStack stack) {
        if (facing == null)
            return;
        gears[facing.getIndex()] = stack;
        world.playSound(null,pos,RegistryHandler.GEAR_ADD,SoundCategory.BLOCKS,1.0f,1.0f);
        markDirty();       
    }

    @Override
    public ItemStack detachGear(EnumFacing facing) {
        if (facing == null)
            return ItemStack.EMPTY;
        int index = facing.getIndex();
        ItemStack gear = gears[index];
        gears[index] = ItemStack.EMPTY;
        world.playSound(null,pos,RegistryHandler.GEAR_REMOVE,SoundCategory.BLOCKS,1.0f,1.0f);
        markDirty();
        
        return gear;
    }

    public ItemStack getGear(EnumFacing facing) {
        if (facing == null)
            return ItemStack.EMPTY;
        return gears[facing.getIndex()];
    }

    @Override
    public boolean canAttachGear(EnumFacing facing, ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public int getConnections() {
        return connections;
    }
    
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        EnumFacing attachSide = side;
        if(hand == EnumHand.OFF_HAND)
            return false;
        if(player.isSneaking())
            attachSide = attachSide.getOpposite();
        if (!heldItem.isEmpty() && canAttachGear(attachSide,heldItem) && getGear(attachSide).isEmpty() && MysticalMechanicsAPI.IMPL.isValidGear(heldItem)) {
                ItemStack gear = heldItem.copy();
                gear.setCount(1);
                attachGear(attachSide,gear);
                heldItem.shrink(1);
                if (heldItem.isEmpty()) {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
                capability.onPowerChange();
                return true;
        } else if (!getGear(attachSide).isEmpty()) {
            ItemStack gear = detachGear(attachSide);
            if (!world.isRemote) {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY + player.height / 2.0f, player.posZ, gear));
            }
            capability.onPowerChange();
            return true;
        }
        return false;
    }

    @Override
    public void playSound(int id) {
        int playId = -getPlayId(id) + 1;
        switch (id) {
            case SOUND_SLOW_LV1:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_SLOW_LV1, playId, RegistryHandler.GEARBOX_SLOW_LV1, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_SLOW_LV2:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_SLOW_LV2, playId, RegistryHandler.GEARBOX_SLOW_LV2, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_SLOW_LV3:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_SLOW_LV3, playId, RegistryHandler.GEARBOX_SLOW_LV3, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_MID_LV1:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_MID_LV1, playId, RegistryHandler.GEARBOX_MID_LV1, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_MID_LV2:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_MID_LV2, playId, RegistryHandler.GEARBOX_MID_LV2, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_MID_LV3:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_MID_LV3, playId, RegistryHandler.GEARBOX_MID_LV3, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_FAST_LV1:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_FAST_LV1, playId, RegistryHandler.GEARBOX_FAST_LV1, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_FAST_LV2:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_FAST_LV2, playId, RegistryHandler.GEARBOX_FAST_LV2, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
            case SOUND_FAST_LV3:
                MysticalMechanics.proxy.playMachineSound(this, SOUND_FAST_LV2, playId, RegistryHandler.GEARBOX_FAST_LV3, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f, (float)pos.getY()+0.5f, (float)pos.getZ()+0.5f);
                break;
        }
        soundsPlaying.put(id,playId);
    }

    @Override
    public void stopSound(int id) {
        soundsPlaying.put(id,-getPlayId(id));
    }

    @Override
    public int getPlayId(int id) {
        if(!soundsPlaying.containsKey(id))
            return 0;
        return soundsPlaying.get(id);
    }

    @Override
    public int[] getSoundIDs() {
        return SOUND_IDS;
    }

    @Override
    public boolean shouldPlaySound(int id) {
        double power = capability.getPower(null);
        int level = getSoundLevel();
        int speedindex = getSpeedindex(power);
        return speedindex > 0 && level > 0 && id == SOUND_IDS[speedindex - 1 + level];
    }

    private int getSpeedindex(double power) {
        int speedindex = 0;
        if(power > 50)
            speedindex = 3;
        else if(power > 25)
            speedindex = 2;
        else if(power > 0)
            speedindex = 1;
        return speedindex;
    }

    @Override
    public float getCurrentPitch(int id, float pitch) {
        double power =  capability.getPower(null);
        int speedindex = getSpeedindex(power);
        if(speedindex == 1)
            return (float) (power*2 / 25.0);
        else if(speedindex == 2)
            return (float) (power*2 / 50.0);
        else if(speedindex == 3)
            return (float) (power*2 / 100.0);
        return 0;
    }

    private int getSoundLevel() {
        int level = 0;
        for (EnumFacing.Axis axis : EnumFacing.Axis.values()) {
            if(!getGear(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE,axis)).isEmpty() || !getGear(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE,axis)).isEmpty())
                level++;
        }
        return level;
    }

    @Override
    public void markDirty() {
        super.markDirty();       
        Misc.syncTE(this, isBroken);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        for (int i = 0; i < 6; i++) {
            if (!world.isRemote) {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, gears[i]));
            }
            gears[i] = ItemStack.EMPTY;
        }
        isBroken = true;
        capability.setPower(0, this.from);
        updateNeighbors();
    }

    @Override
    public void update() {
        if (world.isRemote) {
            handleSound();
            for(EnumFacing facing : EnumFacing.VALUES) {
                updateAngle(facing);
                ItemStack gear = getGear(facing);
                IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gear);
                behavior.visualUpdate(this,facing,gear);
            }            
        }
    }

    protected void updateAngle(EnumFacing facing) {
        lastAngles[facing.getIndex()] = angles[facing.getIndex()];
        angles[facing.getIndex()] += capability.getPower(facing);
    }

    public double getAngle(EnumFacing face) {
        return angles[face.getIndex()];
    }

    public double getLastAngle(EnumFacing face) {
        return lastAngles[face.getIndex()];
    }

    public void rotateTile(World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing currentFacing = state.getValue(BlockGearbox.facing);

        capability.setPower(0,null);
        ItemStack[] temp = new ItemStack[gears.length];
        for (int i = 0; i < gears.length; i++) {
            temp[i] = gears[i];
        }
        for (int i = 0; i < gears.length; i++) {
            EnumFacing facing = EnumFacing.getFront(i).rotateAround(side.getAxis());
            gears[facing.getIndex()] = temp[i];
        }
        from = from.rotateAround(side.getAxis());
        world.setBlockState(pos,state.withProperty(BlockGearbox.facing,currentFacing.rotateAround(side.getAxis())));
        capability.onPowerChange();
        //markDirty();
    }
}
