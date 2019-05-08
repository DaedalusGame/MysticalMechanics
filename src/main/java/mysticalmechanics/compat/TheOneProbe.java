package mysticalmechanics.compat;

import mcjty.theoneprobe.api.*;
import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.IAxle;
import mysticalmechanics.api.IGearbox;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TheOneProbe implements Function<ITheOneProbe, Void>, IProbeInfoProvider {
    public static ITheOneProbe probe;

    public static int ELEMENT_POWERUNIT;

    public static void init() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mysticalmechanics.compat.TheOneProbe");
    }

    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        ELEMENT_POWERUNIT = probe.registerElementFactory(new PowerUnit.Factory());
        probe.registerProvider(this);
        return null;
    }

    @Override
    public String getID() {
        return MysticalMechanics.MODID;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tile = world.getTileEntity(data.getPos());
        IGearbox gearbox = null;

        if (tile instanceof IGearbox)
            gearbox = (IGearbox) tile;
        if (tile instanceof IAxle) {
            IAxle axle = (IAxle) tile;
            probeInfo.text(TextStyleClass.LABEL + IProbeInfo.STARTLOC + "mysticalmechanics.probe.axle_length" + IProbeInfo.ENDLOC + " " + TextStyleClass.INFO + axle.getLength());
        }

        EnumFacing currentFacing = data.getSideHit();

        if (tile != null) {
            List<MechInfoStruct> info = new ArrayList<>();
            if(mode == ProbeMode.EXTENDED)
            for (EnumFacing facing : EnumFacing.VALUES) {
                boolean forceWrite = false;
                ItemStack gear = ItemStack.EMPTY;
                if (gearbox != null && gearbox.canAttachGear(facing)) {
                    gear = gearbox.getGear(facing);
                    if(gear.isEmpty())
                        continue;
                    forceWrite = true;
                }
                if (tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
                    IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
                    boolean input = capability.isInput(facing);
                    boolean output = capability.isOutput(facing);
                    double power = capability.getVisualPower(facing);
                    MechInfoStruct struct;
                    if (input && output)
                        struct = new MechInfoStruct(MechInfoType.Both, power, facing);
                    else if (input)
                        struct = new MechInfoStruct(MechInfoType.Input, power, facing);
                    else if (output)
                        struct = new MechInfoStruct(MechInfoType.Output, power, facing);
                    else
                        continue;
                    if(forceWrite)
                        addMechPowerData(mode,probeInfo,struct,gear,currentFacing);
                    else
                        info.add(struct);
                }
            }
            Map<MechInfoType, List<MechInfoStruct>> grouped = info.stream().collect(Collectors.groupingBy(mechInfoStruct -> mechInfoStruct.type));
            if(grouped.entrySet().stream().allMatch(entry -> entry.getValue().stream().distinct().count() <= 1)) {
                for (List<MechInfoStruct> struct : grouped.values()) {
                    if(!struct.isEmpty())
                        addMechPowerData(mode,probeInfo,struct.get(0),ItemStack.EMPTY,currentFacing);
                }
            } else {
                for (MechInfoStruct struct : info) {
                    addMechPowerData(mode,probeInfo,struct,ItemStack.EMPTY,currentFacing);
                }
            }
        }
    }

    private void addMechPowerData(ProbeMode mode, IProbeInfo probeInfo, MechInfoStruct struct, ItemStack gear, EnumFacing facing) {
        boolean input = struct.type == MechInfoType.Input || struct.type == MechInfoType.Both;
        boolean output = struct.type == MechInfoType.Output || struct.type == MechInfoType.Both;
        probeInfo.element(new PowerUnit(struct.power,input,output,gear,struct.facing));
    }

    enum MechInfoType {
        Input,
        Output,
        Both,
    }

    class MechInfoStruct {
        MechInfoType type;
        double power;
        EnumFacing facing;

        public MechInfoStruct(MechInfoType type, double power, EnumFacing facing) {
            this.type = type;
            this.power = power;
            this.facing = facing;
        }

        public MechInfoType getType() {
            return type;
        }

        public double getPower() {
            return power;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MechInfoStruct)
                return equals((MechInfoStruct) obj);
            return super.equals(obj);
        }

        private boolean equals(MechInfoStruct struct) {
            return type.equals(struct.type) && power == struct.power;
        }

        @Override
        public int hashCode() {
            return type.hashCode() ^ Double.hashCode(power);
        }
    }
}
