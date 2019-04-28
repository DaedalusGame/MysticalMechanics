package mysticalmechanics.tileentity;

import java.util.ArrayList;
import java.util.List;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockGearbox;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMergebox extends TileEntityGearbox {
	//public int connections = 0;
	
    @Override
    public DefaultMechCapability createCapability() {
        return new MergeboxMechCapability();
    }

    @Override
    public void update() {
        super.update();
        ((MergeboxMechCapability)capability).reduceWait();
    }

    @Override
    protected void updateAngle(EnumFacing facing) {
        double value = 0;
        if(capability.isOutput(facing)) {
            value = capability.getPower(facing);
        } else {
            value = ((MergeboxMechCapability)capability).powerValues[facing.getIndex()];
        }

        lastAngles[facing.getIndex()] = angles[facing.getIndex()];
        angles[facing.getIndex()] += value;
    }

    @Override
    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        
        //manages Mergeboxes input;
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity t = world.getTileEntity(getPos().offset(f));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()) && capability.isInput(f)) {
                if (t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).isOutput(f.getOpposite()) && !getGear(f).isEmpty()) {
                    capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
                } else if (getGear(f).isEmpty() && capability.isInput(f)) {
                    capability.setPower(0, f);
                }
            }
        }
        
        connections = 0;
        List<EnumFacing> toUpdate = new ArrayList<>();
        for (EnumFacing f : EnumFacing.values()) {
            if (f != null && f != from) {
                TileEntity t = world.getTileEntity(getPos().offset(f));
                if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())) {
                    if (!getGear(f).isEmpty() && !toUpdate.contains(f)) {
                    	toUpdate.add(f);
                    	connections++;                    	
                    }else if(getGear(f).isEmpty() && toUpdate.contains(f)) {
                    	toUpdate.remove(f);
                    	connections--;
                    }                   
                }
            }                  
        }       
        
        //manages Mergeboxes output
        if (state.getBlock() instanceof BlockGearbox) {
            from = state.getValue(BlockGearbox.facing);            
            TileEntity t = world.getTileEntity(getPos().offset(from));

            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite())) {
            	if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).isInput(from.getOpposite()) && !getGear(from).isEmpty()) {
            		t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).setPower(capability.getPower(from), from.getOpposite());
            	}else if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).isInput(from.getOpposite())) {
            		t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).setPower(0, from.getOpposite());
            	}
            }
        }        
        markDirty();
    }
    
    //repurposing this for inputTracking;
    @Override
    public int getConnections() {
        return connections;
    }

    private class MergeboxMechCapability extends DefaultMechCapability {
        public double[] powerValues = {0,0,0,0,0,0};
        public int waitTime;

        public void reduceWait() {
            if (waitTime > 0) {
                waitTime--;
                if (waitTime <= 0) {
                    updateNeighbors();
                }
            }
        }

        @Override
        public void onPowerChange() {
            TileEntityGearbox box = TileEntityMergebox.this;
            box.updateNeighbors();
            box.markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
            ItemStack gearStack = getGear(from);
            if (from != null && getGear(from).isEmpty()) {
                return 0;
            }

            IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
            double changedPower = 0;

            //need to work out solution for null checks that aren't the renderer.
            if (from == null && getConnections() != 0) {//|| from == null
                changedPower = power / ((double) (Math.max(1, getConnections())));
            } else if (isOutput(from) && !getGear(from).isEmpty() && getConnections() != 0) {
                changedPower = Math.max(0, getPowerInternal());
            }
            return behavior.transformPower(TileEntityMergebox.this, from, gearStack, changedPower);
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            ItemStack gearStack = getGear(from);
        	if(from != null && !isOutput(from)) {
        		double oldPower = powerValues[from.getIndex()];
        		if(!gearStack.isEmpty()) {
                    IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
                    value = behavior.transformPower(TileEntityMergebox.this,from,gearStack,value);
                }
        		if(oldPower != value && (value == 0 || !gearStack.isEmpty())) {
        			powerValues[from.getIndex()] = value;
                    waitTime = 20;
        			onPowerChange();
        		}
        	}else if(from == null && TileEntityMergebox.this.isBroken) {
        		for(EnumFacing face : EnumFacing.values()) {
        			powerValues[face.getIndex()] = 0;
        			onPowerChange();
        		}
        	}           
        }     
        
        private double getPowerInternal() {
            double adjustedPower;
            if(waitTime > 0)
                adjustedPower = 0;
            else {
                adjustedPower = 0;
                double equalPower = Double.POSITIVE_INFINITY;
                for (EnumFacing facing : EnumFacing.VALUES) {
                    if (isOutput(facing))
                        continue;
                    double power = powerValues[facing.getIndex()];
                    if (power > 0)
                        equalPower = Math.min(equalPower, power);
                }
                for (EnumFacing face : EnumFacing.values()) {
                    double power = powerValues[face.getIndex()];
                    if (!isOutput(face) && Misc.isRoughlyEqual(equalPower, power)) {
                        adjustedPower += power;
                    }
                }
            }
            if(power != adjustedPower) {
                power = adjustedPower;
                markDirty();
            }
            return power;
        }

        @Override
        public boolean isInput(EnumFacing from) {
            return TileEntityMergebox.this.from != from;
        }

        @Override
        public boolean isOutput(EnumFacing from) {
            return TileEntityMergebox.this.from == from;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            powerValues[EnumFacing.UP.getIndex()] = tag.getDouble("mechPowerUp");
            powerValues[EnumFacing.DOWN.getIndex()] = tag.getDouble("mechPowerDown");
            powerValues[EnumFacing.NORTH.getIndex()] = tag.getDouble("mechPowerNorth");
            powerValues[EnumFacing.SOUTH.getIndex()] = tag.getDouble("mechPowerSouth");
            powerValues[EnumFacing.EAST.getIndex()] = tag.getDouble("mechPowerEast");
            powerValues[EnumFacing.WEST.getIndex()] = tag.getDouble("mechPowerWest");
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);

            tag.setDouble("mechPowerUp",powerValues[EnumFacing.UP.getIndex()]);
            tag.setDouble("mechPowerDown",powerValues[EnumFacing.DOWN.getIndex()]);
            tag.setDouble("mechPowerNorth",powerValues[EnumFacing.NORTH.getIndex()]);
            tag.setDouble("mechPowerSouth", powerValues[EnumFacing.SOUTH.getIndex()]);
            tag.setDouble("mechPowerEast",powerValues[EnumFacing.EAST.getIndex()]);
            tag.setDouble("mechPowerWest",powerValues[EnumFacing.WEST.getIndex()]);
        }
    }
}
