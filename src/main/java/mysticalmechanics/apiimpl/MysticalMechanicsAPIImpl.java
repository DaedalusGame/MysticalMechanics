package mysticalmechanics.apiimpl;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.*;
import mysticalmechanics.api.lubricant.ILubricant;
import mysticalmechanics.api.lubricant.SimpleLubricant;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class MysticalMechanicsAPIImpl implements IMysticalMechanicsAPI {
    public static LinkedHashMap<ResourceLocation, GearStruct> GEAR_REGISTRY = new LinkedHashMap<>();
    public static LinkedHashMap<String, IMechUnit> UNITS = new LinkedHashMap<>();
    private static HashMap<String,IConfigValue> CONFIG_VALUES = new HashMap<>();
    private static LinkedHashMap<ResourceLocation, Function<NBTTagCompound, ILubricant>> LUBRICANT_REGISTRY = new LinkedHashMap<>();

    private IMechUnit unitDefault;
    private boolean unitDirty;

    static int tick;

    public MysticalMechanicsAPIImpl(){
        super();
        MinecraftForge.EVENT_BUS.register(getClass());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START)
            tick++;
    }

    public void registerConfigValue(IConfigValue value) {
        CONFIG_VALUES.put(value.getKey(),value);
    }

    @Override
    public void registerGear(ResourceLocation resourceLocation, Ingredient matcher, IGearBehavior behavior) {
        GEAR_REGISTRY.put(resourceLocation,new GearStruct(matcher, behavior));
    }

    @Override
    public void unregisterGear(ResourceLocation resourceLocation) {
        GEAR_REGISTRY.remove(resourceLocation);
    }

    @Override
    public void modifyGear(ResourceLocation resourceLocation, Function<IGearBehavior, IGearBehavior> modifier) {
        GearStruct struct = GEAR_REGISTRY.get(resourceLocation);
        if(struct != null) {
            struct.behavior = modifier.apply(struct.behavior);
        }
    }

    @Override
    public Iterable<ResourceLocation> getGearKeys() {
        return GEAR_REGISTRY.keySet();
    }

    @Override
    public IGearBehavior getGearBehavior(ItemStack stack) {
        if (!stack.isEmpty())
            for (GearStruct struct : GEAR_REGISTRY.values())
                if (struct.ingredient.apply(stack))
                    return struct.behavior;
        return IGearBehavior.NO_BEHAVIOR;
    }

    @Nonnull
    @Override
    public IGearBehavior getGearBehavior(ResourceLocation resourceLocation) {
        GearStruct gearStruct = GEAR_REGISTRY.get(resourceLocation);
        return gearStruct != null ? gearStruct.behavior : IGearBehavior.NO_BEHAVIOR;
    }

    @Override
    public boolean isValidGear(ItemStack stack) {
        if (!stack.isEmpty())
            for (GearStruct struct : GEAR_REGISTRY.values())
                if (struct.ingredient.apply(stack))
                    return true;
        return false;
    }

    @Override
    public void registerLubricant(ResourceLocation resLoc, Function<NBTTagCompound, ILubricant> generator) {
        LUBRICANT_REGISTRY.put(resLoc,generator);
    }

    @Override
    public void registerSimpleLubricant(SimpleLubricant lubricant) {
        registerLubricant(lubricant.getType(), tag -> lubricant);
    }

    @Override
    public void unregisterLubricant(ResourceLocation resLoc) {
        LUBRICANT_REGISTRY.remove(resLoc);
    }

    @Override
    public ILubricant deserializeLubricant(NBTTagCompound tag) {
        ResourceLocation resLoc = new ResourceLocation(tag.getString("type"));
        Function<NBTTagCompound, ILubricant> generator = LUBRICANT_REGISTRY.get(resLoc);
        if(generator != null)
            return generator.apply(tag);
        return null;
    }

    @Override
    public void registerUnit(IMechUnit unit) {
        UNITS.put(unit.getName(),unit);
        unitDirty = true;
    }

    @Override
    public IMechUnit getDefaultUnit() {
        if(unitDirty) {
            if(!MysticalMechanics.FORCE_UNIT.isEmpty())
                unitDefault = getUnit(MysticalMechanics.FORCE_UNIT);
            if(unitDefault == null)
                unitDefault = getHighestPriorityUnit();
            unitDirty = false;
        }
        return unitDefault;
    }

    private IMechUnit getHighestPriorityUnit() {
        return UNITS.values().stream().max(Comparator.comparingInt(IMechUnit::getPriority)).orElse(null);
    }

    @Override
    public IMechUnit getUnit(String name) {
        return UNITS.get(name);
    }

    @Override
    public Iterable<IMechUnit> getUnits() {
        return UNITS.values();
    }

    @Override
    public Map<String, IConfigValue> getConfigValues() {
        return CONFIG_VALUES;
    }

    @Override
    public IConfigValue getConfigValue(String key) {
        return CONFIG_VALUES.get(key);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return MysticalMechanics.creativeTab;
    }

    @Override
    public void pushPower(TileEntity tileSelf, EnumFacing sideSelf, IMechCapability capSelf, boolean hasGear) {
        TileEntity tileOther = tileSelf.getWorld().getTileEntity(tileSelf.getPos().offset(sideSelf));
        EnumFacing sideOther = sideSelf.getOpposite();
        if(tileOther != null) {
            IMechCapability capOther = tileOther.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, sideOther);
            if(capOther != null && !capSelf.isInput(sideSelf)) {
                if(hasGear)
                    capOther.setPower(capSelf.getPower(sideSelf), sideOther);
                else
                    capOther.setPower(0, sideOther);
            }
        }
    }

    @Override
    public void pullPower(TileEntity tileSelf, EnumFacing sideSelf, IMechCapability capSelf, boolean hasGear) {
        TileEntity tileOther = tileSelf.getWorld().getTileEntity(tileSelf.getPos().offset(sideSelf));
        EnumFacing sideOther = sideSelf.getOpposite();
        boolean hasValidTile = false;
        if(tileOther != null) {
            IMechCapability capOther = tileOther.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, sideOther);
            hasValidTile = capOther != null;
            if (capOther != null && !capSelf.isOutput(sideSelf)) {
                if (hasGear && capOther.isOutput(sideOther))
                    capSelf.setPower(capOther.getPower(sideOther), sideSelf);
                else
                    capSelf.setPower(0, sideSelf);
            }
        }
        if(!hasValidTile)
            capSelf.setPower(0, sideSelf);
    }

    @Override
    public boolean isGearHit(TileEntity tile, EnumFacing facing) {
        return MysticalMechanics.proxy.isGearHit(tile, facing);
    }

    @Override
    public boolean shouldRenderHologram(ItemStack gear, boolean hasGear, boolean sideHit, boolean canAttach) {
        if(sideHit) {
            boolean isHoldingGear = MysticalMechanicsAPI.IMPL.isValidGear(gear);
            boolean gearFits = isHoldingGear && canAttach;
            if (!hasGear && !gearFits) {
                return false;
            }
        }
        return sideHit;
    }

    @Override
    public void renderGear(ItemStack gear, ItemStack gearHologram, boolean renderHologram, float partialTicks, double offset, double scale, float angle) {
        float totalTick = tick + partialTicks;
        MysticalMechanics.proxy.renderGear(gear,gearHologram,renderHologram,totalTick,offset,scale,angle);
    }

    @Override
    public void renderAxle(ModelResourceLocation resLoc, EnumFacing.Axis axis, float angle) {
        MysticalMechanics.proxy.renderAxle(resLoc, axis, angle);
    }

    @Override
    public void syncAngle(TileEntity tile, EnumFacing checkDirection) {
        if (!(tile instanceof IHasRotation))
            return;
        BlockPos axlePos = tile.getPos().offset(checkDirection);
        TileEntity axleTile = tile.getWorld().getTileEntity(axlePos);
        if (axleTile instanceof IHasRotation) {
            IHasRotation axle = (IHasRotation) axleTile;
            if (axle.hasRotation(checkDirection.getOpposite())) {
                double angle = axle.getAngle(checkDirection.getOpposite());
                double lastAngle = axle.getLastAngle(checkDirection.getOpposite());
                ((IHasRotation) tile).setRotation(checkDirection,angle,lastAngle);
            }
        }
    }

    static class GearStruct {
        Ingredient ingredient;
        IGearBehavior behavior;

        public GearStruct(Ingredient ingredient, IGearBehavior behavior) {
            this.ingredient = ingredient;
            this.behavior = behavior;
        }
    }
}
