package mysticalmechanics.tileentity;

import java.util.ArrayList;
import java.util.List;

import mysticalmechanics.api.*;
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
    protected double getInternalPower(EnumFacing facing) {
        return ((MergeboxMechCapability)capability).getInternalPower(facing);
    }

    @Override
    protected double getExternalPower(EnumFacing facing) {
        return ((MergeboxMechCapability)capability).getExternalPower(facing);
    }

    @Override
    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        
        //manages Mergeboxes input;
        for (EnumFacing f : EnumFacing.VALUES) {
            MysticalMechanicsAPI.IMPL.pullPower(this, f, capability, !getGear(f).isEmpty());
        }
        
        connections = 0;
        List<EnumFacing> toUpdate = new ArrayList<>();
        for (EnumFacing f : EnumFacing.VALUES) {
            if (f != from) {
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
            MysticalMechanicsAPI.IMPL.pushPower(this, from, capability, !getGear(from).isEmpty());
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
        public double[] powerValuesExternal = {0,0,0,0,0,0};
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
            box.shouldUpdate = true;
            box.markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
            GearHelper gearHelper = getGearHelper(from);
            if (gearHelper != null && gearHelper.isEmpty()) {
                return 0;
            }

            double unchangedPower = getInternalPower(from);

            if (gearHelper == null)
                return unchangedPower;

            IGearBehavior behavior = gearHelper.getBehavior();
            return behavior.transformPower(TileEntityMergebox.this, from, gearHelper.getGear(), gearHelper.getData(), unchangedPower);
        }

        @Override
        public double getVisualPower(EnumFacing from) {
            GearHelper gearHelper = getGearHelper(from);
            if (gearHelper != null && gearHelper.isEmpty()) {
                return 0;
            }

            double unchangedPower;
            if(isInput(from))
                unchangedPower = getExternalPower(from);
            else
                unchangedPower = getInternalPower(from);

            if (gearHelper == null)
                return unchangedPower;

            IGearBehavior behavior = gearHelper.getBehavior();
            return behavior.transformVisualPower(TileEntityMergebox.this, from, gearHelper.getGear(), gearHelper.getData(), unchangedPower);
        }

        private double getInternalPower(EnumFacing from) {
            //need to work out solution for null checks that aren't the renderer.
            if (isOutput(from) && !getGear(from).isEmpty() && getConnections() != 0) {
                return Math.max(0, getPowerInternal());
            } else if (from != null && !getGear(from).isEmpty()) {
                return powerValues[from.getIndex()];
            } else {
                return 0;
            }
        }

        private double getExternalPower(EnumFacing from) {
            if(from != null && isInput(from))
                return powerValuesExternal[from.getIndex()];
            else
                return 0;
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            GearHelper gearHelper = getGearHelper(from);
            if(from == null) {
                for (int i = 0; i < powerValues.length; i++) {
                    powerValues[i] = 0;
                }
                onPowerChange();
            }
        	if(from != null && gearHelper != null && !isOutput(from)) {
        		double oldPower = powerValues[from.getIndex()];
        		powerValuesExternal[from.getIndex()] = value;
        		if(!gearHelper.isEmpty()) {
                    IGearBehavior behavior = gearHelper.getBehavior();
                    value = behavior.transformPower(TileEntityMergebox.this,from,gearHelper.getGear(),gearHelper.getData(),value);
                }
        		if(oldPower != value && (value == 0 || !gearHelper.isEmpty())) {
        			powerValues[from.getIndex()] = value;
                    waitTime = 20;
        			onPowerChange();
        		}
        	} else if(from == null && TileEntityMergebox.this.isBroken) {
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
            waitTime = tag.getInteger("waitTime");
            powerValues[EnumFacing.UP.getIndex()] = tag.getDouble("mechPowerUp");
            powerValues[EnumFacing.DOWN.getIndex()] = tag.getDouble("mechPowerDown");
            powerValues[EnumFacing.NORTH.getIndex()] = tag.getDouble("mechPowerNorth");
            powerValues[EnumFacing.SOUTH.getIndex()] = tag.getDouble("mechPowerSouth");
            powerValues[EnumFacing.EAST.getIndex()] = tag.getDouble("mechPowerEast");
            powerValues[EnumFacing.WEST.getIndex()] = tag.getDouble("mechPowerWest");
            powerValuesExternal[EnumFacing.UP.getIndex()] = tag.getDouble("mechPowerExternalUp");
            powerValuesExternal[EnumFacing.DOWN.getIndex()] = tag.getDouble("mechPowerExternalDown");
            powerValuesExternal[EnumFacing.NORTH.getIndex()] = tag.getDouble("mechPowerExternalNorth");
            powerValuesExternal[EnumFacing.SOUTH.getIndex()] = tag.getDouble("mechPowerExternalSouth");
            powerValuesExternal[EnumFacing.EAST.getIndex()] = tag.getDouble("mechPowerExternalEast");
            powerValuesExternal[EnumFacing.WEST.getIndex()] = tag.getDouble("mechPowerExternalWest");
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setInteger("waitTime",waitTime);
            tag.setDouble("mechPowerUp",powerValues[EnumFacing.UP.getIndex()]);
            tag.setDouble("mechPowerDown",powerValues[EnumFacing.DOWN.getIndex()]);
            tag.setDouble("mechPowerNorth",powerValues[EnumFacing.NORTH.getIndex()]);
            tag.setDouble("mechPowerSouth", powerValues[EnumFacing.SOUTH.getIndex()]);
            tag.setDouble("mechPowerEast",powerValues[EnumFacing.EAST.getIndex()]);
            tag.setDouble("mechPowerWest",powerValues[EnumFacing.WEST.getIndex()]);
            tag.setDouble("mechPowerExternalUp",powerValuesExternal[EnumFacing.UP.getIndex()]);
            tag.setDouble("mechPowerExternalDown",powerValuesExternal[EnumFacing.DOWN.getIndex()]);
            tag.setDouble("mechPowerExternalNorth",powerValuesExternal[EnumFacing.NORTH.getIndex()]);
            tag.setDouble("mechPowerExternalSouth", powerValuesExternal[EnumFacing.SOUTH.getIndex()]);
            tag.setDouble("mechPowerExternalEast",powerValuesExternal[EnumFacing.EAST.getIndex()]);
            tag.setDouble("mechPowerExternalWest",powerValuesExternal[EnumFacing.WEST.getIndex()]);
        }
    }
}
