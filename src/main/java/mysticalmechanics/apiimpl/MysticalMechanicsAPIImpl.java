package mysticalmechanics.apiimpl;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

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

    private IMechUnit unitDefault;
    private boolean unitDirty;

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
        if(tileOther != null) {
            IMechCapability capOther = tileOther.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, sideOther);
            if (capOther != null && !capSelf.isOutput(sideSelf)) {
                if (hasGear && capOther.isOutput(sideOther))
                    capSelf.setPower(capOther.getPower(sideOther), sideSelf);
                else
                    capSelf.setPower(0, sideSelf);
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
