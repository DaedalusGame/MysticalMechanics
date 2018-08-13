package mysticalmechanics.apiimpl;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IMysticalMechanicsAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

public class MysticalMechanicsAPIImpl implements IMysticalMechanicsAPI {
    public static LinkedHashMap<ResourceLocation, GearStruct> GEAR_REGISTRY = new LinkedHashMap<>();

    @Override
    public void registerGear(ResourceLocation resourceLocation, Ingredient matcher, IGearBehavior behavior) {
        GEAR_REGISTRY.put(resourceLocation,new GearStruct(matcher, behavior));
    }

    @Override
    public void unregisterGear(ResourceLocation resourceLocation) {
        GEAR_REGISTRY.remove(resourceLocation);
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

    static class GearStruct {
        Ingredient ingredient;
        IGearBehavior behavior;

        public GearStruct(Ingredient ingredient, IGearBehavior behavior) {
            this.ingredient = ingredient;
            this.behavior = behavior;
        }
    }
}
