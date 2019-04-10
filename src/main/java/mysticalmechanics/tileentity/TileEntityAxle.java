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
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityAxle extends TileEntity implements ITickable, IAxle {
	BlockPos front;
	BlockPos back;
    public double angle, lastAngle;
    private boolean isBroken;
    private EnumFacing facing;	
	private EnumFacing inputSide;
    
    public DefaultMechCapability capability = new DefaultMechCapability(){
        @Override
        public void onPowerChange(){
            updateNeighbors();
            markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
        	if(from == null) {
    			//this should only really be called on block break.
    			return this.power;
    		}else if (isValidSide(from)&&isOutput(from)){				
    			return this.power;
    		}	
    		return 0;				
        }
        
        @Override
        public void setPower(double value, EnumFacing from) {
        	if(from == null && isBroken()) {
    			capability.power = 0;
    			onPowerChange();
    		}else if(isInput(from)){					
    			double oldPower = power; 
    			if (isValidSide(from)&& oldPower != value){			
    				this.power = value;				
    				onPowerChange();
    			}
    		}       	
        }
        
        @Override
        public boolean isInput(EnumFacing from) {
        	checkAndSetInput(from);
    		if(inputSide != null && from != null) {
    			return inputSide == from;
    		}
    		return false;
        }
        
        @Override
        public boolean isOutput(EnumFacing from) {
        	if(from != null && inputSide != null) {			
    			return inputSide.getOpposite() == from;
    		}
    		return false;
        }
    };  

    public void updatePower() {
    	if(facing == null) {
			setConnection();
		}
		
		EnumFacing frontFacing = world.getBlockState(this.pos).getValue(BlockAxle.facing);
		EnumFacing backFacing = frontFacing.getOpposite();
		TileEntity frontTile = world.getTileEntity(front);
		TileEntity backTile = world.getTileEntity(back);
		
		//input updates
		if (frontTile != null && frontTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing)){			
			if(frontTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing).isOutput(frontFacing)&&capability.isInput(backFacing)){				
				capability.setPower(frontTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing).getPower(backFacing), frontFacing);
			}
		}			
					
		if (backTile != null && backTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing)){			
			if(backTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing).isOutput(backFacing)&&capability.isInput(frontFacing)) {				
				capability.setPower(backTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing).getPower(frontFacing), backFacing);						
			}
		}		
				
		//output updates
		if (frontTile != null && frontTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing)){		
			if(frontTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing).isInput(frontFacing)){				
				frontTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, backFacing).setPower(capability.getPower(backFacing),frontFacing);
			}
		}			
					
		if (backTile != null && backTile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing)){			
			if(backTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing).isInput(backFacing)) {				
				backTile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, frontFacing).setPower(capability.getPower(frontFacing),backFacing);
			}
		}   	
       
    } 
    
    //required due to IAxle not currently used.
    public BlockPos getConnection(AxisDirection dir){    
        return dir == AxisDirection.POSITIVE ? front : back;
    }    
    //also required by IAxle and not currently used.
    public TileEntityAxle getConnectionTile(AxisDirection dir) {
        TileEntity tile = world.getTileEntity(getConnection(dir));
        if(tile instanceof TileEntityAxle)
            return (TileEntityAxle) tile;
        return null;
    }
    
    public TileEntityAxle getConnectionTile(BlockPos pos, EnumFacing facing) {
		setConnection();
		TileEntity tile = world.getTileEntity(pos);
		if (tile!=null && tile instanceof TileEntityAxle)
			if(((TileEntityAxle)tile).getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing).isOutput(facing.getOpposite()))
			return (TileEntityAxle) tile;
		return null;
	}

    public void updateNeighbors(){
    	updatePower();
        //updateConnection();
    }

    public TileEntityAxle(){
        super();
    }
    
    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		//switched facing and inputSide to a string value for until i can figure out the issue with reading int value.
		tag.setDouble("mech_power", capability.power);
		if (facing != null) {
            tag.setString("facing", facing.getName());
        }		
		if (inputSide != null) {
            tag.setString("inputSide", inputSide.getName());
        }
		if(front != null) {
			int[] pos = {
				front.getX(), front.getY(),front.getZ()				
			};			
        	tag.setIntArray("front", pos);       	
        }
		if(back != null) {
			int[] pos = {
				back.getX(), back.getY(),back.getZ()				
			};			
        	tag.setIntArray("back", pos);       	
        }		
		return tag;
	}  
    
    @Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		//facing and inputSide aren't being read correctly by Enumfacing.getFront I need to figure out why.
		capability.power = tag.getDouble("mech_power");
		if (tag.hasKey("facing")) {
            this.facing = EnumFacing.byName(tag.getString("from"));
        }		
		if(tag.hasKey("inputSide")) {
			this.inputSide = EnumFacing.byName(tag.getString("inputSide"));
		}		
		if(tag.hasKey("front")){
			int[] pos = tag.getIntArray("front");
			this.front = new BlockPos(pos[0],pos[1],pos[2]);
		}
		if(tag.hasKey("back")){
			int[] pos = tag.getIntArray("back");
			this.back = new BlockPos(pos[0],pos[1],pos[2]);
		}		
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
		if(this.facing == null) {
			setConnection();
		}
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY) {
			IBlockState state = world.getBlockState(getPos());						
			if (state.getBlock() instanceof BlockAxle) {				
				if (facing != null && isValidSide(facing)) {
					return true;
				}
			}
		}
		return super.hasCapability(capability, facing);
	}   

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
        	@SuppressWarnings("unchecked")
			T result = (T) this.capability;
            return result;
        }
        return super.getCapability(capability, facing);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    	isBroken = true;
        capability.setPower(0f,null);
    }
    
    public boolean isValidSide(EnumFacing facing){
		return facing == this.facing || facing == this.facing.getOpposite();
	}
    
    public boolean isValidSide(BlockPos from) {		
		return from.equals(front) ||from.equals(back);
	}
    
    public void setConnection() {
		if(facing == null || front == null || back == null) {
		IBlockState state = world.getBlockState(getPos());
		facing = state.getValue(BlockAxle.facing).getOpposite();
		front = getPos().offset(facing);
		back = getPos().offset(facing.getOpposite());		
		markDirty();
		}
	}
    
    private EnumFacing comparePosToSides(BlockPos from) {
		if(from.equals(front)) {
			return world.getBlockState(this.pos).getValue(BlockAxle.facing).getOpposite();
		}else if(from.equals(back)) {				
			return world.getBlockState(this.pos).getValue(BlockAxle.facing);
		}
		return null;			
    }
    
    private void checkAndSetInput(EnumFacing from) {
		if(from != null) {
			TileEntity t = world.getTileEntity(getPos().offset(from));
			if(t!=null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from)) {				
				//if the tile entity is a output and we dont have power set the inputside. 
				if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from).isOutput(from.getOpposite())&& capability.power == 0) {					
					this.inputSide = from;					
				}
			}
		}
		
	}
    
    public void neighborChanged(BlockPos from) {
		setConnection();
		if(isValidSide(from)) {			
			checkAndSetInput(comparePosToSides(from));		
			updateNeighbors();
		}
	}

    @Override
    public void update() {
        if(world.isRemote) {
            lastAngle = angle;
            angle += capability.getPower(null);
        }
    }
    
    @Override
    public void markDirty() {
        super.markDirty();       
        Misc.syncTE(this, isBroken);
    }
    
	public boolean isBroken() {		
		return this.isBroken;
	}
}
