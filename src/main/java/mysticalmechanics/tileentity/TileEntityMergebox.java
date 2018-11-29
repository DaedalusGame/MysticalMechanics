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
        ((MergeboxMechCapability)capability).reduceWait();
    }

    @Override
    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity t = world.getTileEntity(getPos().offset(f));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()) && !getGear(f).isEmpty())
                capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
            else
                capability.setPower(0, f);
        }
        if (state.getBlock() instanceof BlockGearbox) {
            from = state.getValue(BlockGearbox.facing);
            TileEntity t = world.getTileEntity(getPos().offset(from));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()) && !getGear(from).isEmpty())
                t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, from.getOpposite()).setPower(capability.getPower(from), from.getOpposite());
        }
        markDirty();
    }

    private class MergeboxMechCapability extends DefaultMechCapability {
        public double[] power = new double[6];
        public int waitTime;
        int maxPower;

        public void reduceWait() {
            if (waitTime > 0) {
                waitTime--;
                if (waitTime <= 0) {
                    recalculateOutput();
                }
            }
        }

        public void recalculateOutput() {
            double equalPower = Double.POSITIVE_INFINITY;
            for (EnumFacing facing : EnumFacing.VALUES) {
                if (facing == TileEntityMergebox.this.from)
                    continue;
                double power = getPower(facing);
                if(power > 0)
                equalPower = Math.min(equalPower, power);
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                if (facing == TileEntityMergebox.this.from)
                    continue;
                double power = getPower(facing);
                if (equalPower == power)
                    maxPower += power;
            }
            onPowerChange();
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
            if (from != null && gearStack.isEmpty()) {
                return 0;
            }
            if (from == null || from == TileEntityMergebox.this.from) {
                return maxPower;
            }
            IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
            return behavior.transformPower(TileEntityMergebox.this, from, gearStack, power[from.getIndex()]);
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            ItemStack gearStack = getGear(from);
            if(from == TileEntityMergebox.this.from)
                return;
            if (from != null && gearStack.isEmpty())
                setPowerInternal(0, from);
            setPowerInternal(value, from);
        }

        public void setPowerInternal(double value, EnumFacing enumFacing) {
            if (enumFacing == null)
                for (int i = 0; i < 6; i++)
                    power[i] = value;
            else {
                double oldPower = power[enumFacing.getIndex()];
                this.power[enumFacing.getIndex()] = value;
                if (oldPower != value) {
                    maxPower = 0;
                    waitTime = 20;
                    onPowerChange();
                }
            }
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
