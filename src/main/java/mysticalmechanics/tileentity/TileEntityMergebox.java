package mysticalmechanics.tileentity;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockGearbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMergebox extends TileEntityGearbox {
    @Override
    public DefaultMechCapability createCapability() {
        return new MergeboxMechCapability();
    }

    @Override
    public void update() {
        super.update();        
    }

    @Override
    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        
        //manages Mergeboxes input;
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity t = world.getTileEntity(getPos().offset(f));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()) && !getGear(f).isEmpty() && capability.isInput(f)) {
            	if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).isOutput(f.getOpposite())) {
            		capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
            	}
            }else if(getGear(f).isEmpty() && capability.isInput(f)) {
            	capability.setPower(0, f);
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

    private class MergeboxMechCapability extends DefaultMechCapability {
        public double[] powerValues = {0,0,0,0,0,0};
       
        @Override
        public void onPowerChange() {
            TileEntityGearbox box = TileEntityMergebox.this;
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
             double changedPower = 0;
             
             //need to work out solution for null checks that aren't the renderer.
             if (from == null) {//|| from == null
                 changedPower = capability.power / ((double) (Math.max(1, getConnections())));
             } else {
            	 changedPower = ((double) Math.max(0, getPowerInternal()));
             }
             return behavior.transformPower(TileEntityMergebox.this, from, gearStack, changedPower);           
        }

        @Override
        public void setPower(double value, EnumFacing from) {
        	if(from != null && !isOutput(from) && !getGear(from).isEmpty()) {
        		double oldPower = powerValues[from.getIndex()];
        		if(oldPower!=value) {
        			powerValues[from.getIndex()] = value;
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
        	double adjustedPower = 0;
        	for(EnumFacing face : EnumFacing.values()) {
        		if(!isOutput(face)) {
        			adjustedPower += powerValues[face.getIndex()];
        		}
        	}
        	capability.power = adjustedPower;
        	return capability.power;
        }

        @Override
        public boolean isInput(EnumFacing from) {
            return TileEntityMergebox.this.from != from;
        }

        @Override
        public boolean isOutput(EnumFacing from) {
            return TileEntityMergebox.this.from == from;
        }
    }
}
