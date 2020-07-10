package mysticalmechanics.tileentity;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import mysticalmechanics.api.*;
import mysticalmechanics.block.BlockConverterBWM;
import mysticalmechanics.util.Misc;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityConverterBWM extends TileEntity implements ITickable, IGearbox {
    protected boolean isBroken;

    ConverterMystMechCapability capabilityMystMech;
    ConverterBWMCapability capabilityBWM;

    GearHelperTile gear;

    public TileEntityConverterBWM() {
        capabilityBWM = new ConverterBWMCapability();
        capabilityMystMech = new ConverterMystMechCapability();
        gear = new GearHelperTile(this, null){
            @Override
            public EnumFacing getFacing() {
                return getSideMystMech();
            }
        };
    }

    public boolean canConvertToBWM() {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockConverterBWM)
            return !state.getValue(BlockConverterBWM.on);
        else
            return false;
    }

    public boolean canConvertToMM() {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockConverterBWM)
            return state.getValue(BlockConverterBWM.on);
        else
            return false;
    }

    private int convertToBWM() {
        if(canConvertToBWM() && capabilityMystMech.power >= 1)
            return (int) Math.ceil(Math.log(capabilityMystMech.power*0.1+1.0) / Math.log(2));
        else
            return 0;
    }

    private double convertToMystMech() {
        if(canConvertToMM())
            return (Math.pow(2,capabilityBWM.power)-1);
        else
            return 0;
    }

    public EnumFacing getSideBWM() {
        return getFacing().getOpposite();
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockConverterBWM.facing);
    }

    public EnumFacing getSideMystMech() {
        return getFacing();
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            int powerBWM = capabilityBWM.calculateInput();
            if (capabilityBWM.power != powerBWM) {
                capabilityBWM.power = powerBWM;
                capabilityMystMech.onPowerChange();
            }
            gear.visualUpdate(capabilityMystMech.getVisualPower(getSideMystMech()));
        } else {
            double power = capabilityMystMech.getPower(gear.getFacing());
            gear.tick(power);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        capabilityMystMech.writeToNBT(tag);
        tag.setInteger("bwmPower",capabilityBWM.power);
        tag.setTag("side", gear.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        capabilityMystMech.readFromNBT(tag);
        capabilityBWM.power = tag.getInteger("bwmPower");
        gear.readFromNBT(tag.getCompoundTag("side"));
        if(tag.hasKey("gear"))
            gear.setGear(new ItemStack(tag.getCompoundTag("gear")));
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
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || facing == getSideMystMech())) {
            return true;
        }
        if(capability == CapabilityMechanicalPower.MECHANICAL_POWER && facing == getSideBWM()) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || facing == getSideMystMech())) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(capabilityMystMech);
        }
        if(capability == CapabilityMechanicalPower.MECHANICAL_POWER && facing == getSideBWM()) {
            return CapabilityMechanicalPower.MECHANICAL_POWER.cast(capabilityBWM);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public void updateNeighbors() {
        EnumFacing from = getSideMystMech();

        if(capabilityMystMech.isInput(from))
            MysticalMechanicsAPI.IMPL.pullPower(this, from, capabilityMystMech, !getGear(from).isEmpty());
        if(capabilityMystMech.isOutput(from))
            MysticalMechanicsAPI.IMPL.pushPower(this, from, capabilityMystMech, !getGear(from).isEmpty());

        markDirty();
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        EnumFacing attachSide = side;
        if(hand == EnumHand.OFF_HAND)
            return false;
        if(side == getSideBWM()) {
            capabilityBWM.power = 0;
            capabilityMystMech.power = 0;
            capabilityMystMech.onPowerChange();
            boolean newOn = !state.getValue(BlockConverterBWM.on);
            if(newOn)
                player.sendStatusMessage(new TextComponentTranslation("mysticalmechanics.tooltip.bwm_converter.on"),true);
            else
                player.sendStatusMessage(new TextComponentTranslation("mysticalmechanics.tooltip.bwm_converter.off"),true);
            world.setBlockState(pos,state.withProperty(BlockConverterBWM.on,newOn));
            return true;
        }
        if(player.isSneaking())
            attachSide = attachSide.getOpposite();
        if (!heldItem.isEmpty() && canAttachGear(attachSide,heldItem) && getGear(attachSide).isEmpty() && MysticalMechanicsAPI.IMPL.isValidGear(heldItem)) {
            ItemStack gear = heldItem.copy();
            gear.setCount(1);
            attachGear(attachSide,gear,player);
            heldItem.shrink(1);
            if (heldItem.isEmpty()) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            return true;
        } else if (!getGear(attachSide).isEmpty()) {
            ItemStack gear = detachGear(attachSide,player);
            if (!world.isRemote) {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY + player.height / 2.0f, player.posZ, gear));
            }
            return true;
        }
        return false;
    }

    public void rotateTile(World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing currentFacing = state.getValue(BlockConverterBWM.facing);

        capabilityMystMech.setPower(0,null);
        world.setBlockState(pos,state.withProperty(BlockConverterBWM.facing,currentFacing.rotateAround(side.getAxis())));
        capabilityMystMech.onPowerChange();
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        ItemStack stack = gear.detach(player);
        if (!world.isRemote) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
        }
        isBroken = true;
        capabilityMystMech.setPower(0, null);
        updateNeighbors();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, isBroken);
    }

    @Override
    public void attachGear(EnumFacing facing, ItemStack stack, EntityPlayer player) {
        if (!canAttachGear(facing, stack))
            return;
        gear.attach(null, stack);
        capabilityMystMech.onPowerChange();
        world.neighborChanged(pos.offset(getSideMystMech()),blockType,pos);
    }

    @Override
    public ItemStack detachGear(EnumFacing facing, EntityPlayer player) {
        if (!canAttachGear(facing))
            return ItemStack.EMPTY;
        ItemStack removed = gear.detach(null);
        capabilityMystMech.onPowerChange();
        world.neighborChanged(pos.offset(getSideMystMech()),blockType,pos);
        return removed;
    }

    @Override
    public ItemStack getGear(EnumFacing facing) {
        return gear.getGear();
    }

    @Override
    public boolean canAttachGear(EnumFacing facing, ItemStack stack) {
        return canAttachGear(facing) && gear.canAttach(stack);
    }

    @Override
    public boolean canAttachGear(EnumFacing facing) {
        return facing == getSideMystMech();
    }

    @Override
    public int getConnections() {
        return 1;
    }

    private class ConverterBWMCapability extends CapabilityMechanicalPower.Default {
        int power;

        @Override
        public int getMechanicalInput(EnumFacing facing) {
            if(facing == getSideBWM())
                return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
            return 0;
        }

        @Override
        public int getMechanicalOutput(EnumFacing facing) {
            if(facing == getSideBWM())
                return convertToBWM();
            return -1;
        }

        @Override
        public int getMaximumInput(EnumFacing facing) {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMinimumInput(EnumFacing facing) {
            return 0;
        }

        @Override
        public Block getBlock() {
            return getBlockType();
        }

        @Override
        public BlockPos getBlockPos() {
            return getPos();
        }

        @Override
        public World getBlockWorld() {
            return getWorld();
        }
    }

    private class ConverterMystMechCapability extends DefaultMechCapability {

        @Override
        public double getPower(EnumFacing from) {
            if (from == null)
                return super.getPower(from);

            ItemStack gearStack = getGear(from);
            if (gearStack.isEmpty())
                return 0;

            IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
            return behavior.transformPower(TileEntityConverterBWM.this, from, gearStack, getInternalPower(from));
        }

        private double getInternalPower(EnumFacing from) {
            if (from == getSideMystMech())
                return canConvertToMM() ? convertToMystMech() : power;
            else
                return 0;
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            if (from == null)
                super.setPower(value, from);

            if (from == getSideMystMech()) {
                ItemStack gearStack = getGear(from);
                double transformedPower;
                if (gearStack.isEmpty())
                    transformedPower = 0;
                else {
                    IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
                    transformedPower = behavior.transformPower(TileEntityConverterBWM.this, from, gearStack, value);
                }

                if(transformedPower != power) {
                    super.setPower(transformedPower, from);
                    world.neighborChanged(pos.offset(getSideBWM()),blockType,pos);
                    //world.notifyNeighborsOfStateChange(pos, blockType, false);
                }
            }
        }

        @Override
        public double getVisualPower(EnumFacing from) {
            if (from == null)
                return super.getPower(from);

            ItemStack gearStack = getGear(from);
            if (gearStack.isEmpty())
                return 0;

            IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(gearStack);
            return behavior.transformVisualPower(TileEntityConverterBWM.this, from, gearStack, getInternalPower(from));
        }

        @Override
        public void onPowerChange() {
            updateNeighbors();
            markDirty();
        }

        @Override
        public boolean isInput(EnumFacing from) {
            return from == getSideMystMech() && canConvertToBWM();
        }

        @Override
        public boolean isOutput(EnumFacing from) {
            return from == getSideMystMech() && canConvertToMM();
        }
    }
}
