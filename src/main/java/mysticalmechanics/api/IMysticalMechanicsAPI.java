package mysticalmechanics.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

public interface IMysticalMechanicsAPI {
    /**
     * Adds a new gear behavior to the registry.
     *
     * @param resourceLocation key representing a gear behavior registry entry. Must be unique.
     * @param matcher matcher for ItemStacks that will represent this gear behavior.
     * @param behavior an implemented behavior.
     */
    void registerGear(ResourceLocation resourceLocation, Ingredient matcher, IGearBehavior behavior);

    /**
     * Removes a gear behavior from the registry by its key.
     * Use this method to remove or modify the standard behaviors or behaviors added by other dependents.
     *
     * @param resourceLocation key representing a gear behavior registry entry.
     */
    void unregisterGear(ResourceLocation resourceLocation);

    /**
     * Quick method for replacing/augmenting a gear behavior
     *
     * @param resourceLocation key representing a gear behavior registry entry.
     * @param modifier function for how the behavior should be modified
     */
    void modifyGear(ResourceLocation resourceLocation, Function<IGearBehavior,IGearBehavior> modifier);

    /**
     * @return an iterable datastructure of currently registered gear behavior registry keys.
     */
    Iterable<ResourceLocation> getGearKeys();

    /**
     * @param stack an ItemStack representing a gear behavior.
     * @return a gear behavior retrieved by the provided ItemStack, or the empty behavior if none exists.
     */
    @Nonnull IGearBehavior getGearBehavior(ItemStack stack);

    /**
     * @param resourceLocation key representing a gear registry entry.
     * @return a gear behavior retrieved by the provided key, or the empty behavior if none exists.
     */
    @Nonnull IGearBehavior getGearBehavior(ResourceLocation resourceLocation);

    /**
     * @param stack an ItemStack representing a gear.
     * @return whether the provided ItemStack is a valid, registered gear.
     */
    boolean isValidGear(ItemStack stack);

    /**
     * Adds a unit for mechanical power.
     * A unit contains helpers for converting between the unit and the internal power value and helpers for formatting text containing mechanical power information.
     * @see IMechUnit
     * @param unit the unit to add
     */
    void registerUnit(IMechUnit unit);

    /**
     * Use this to get the default unit for mechanical power. Returns an IMechUnit that handles all the formatting and contains extra information.
     * The default unit is picked as follows:
     * - if a unit is forced in the config file and that unit exists, that unit is picked.
     * - otherwise the unit with the highest value for IMechUnit::getPriority is picked.
     *
     * This method is cached and you don't need to worry about calling it multiple times.
     *
     * @return the default unit
     */
    IMechUnit getDefaultUnit();

    /**
     * This method gets a specific unit by name, if it exists; otherwise it returns null.
     *
     * This method is not cached.
     *
     * @param name the name of the unit
     * @return a specific unit, if it exists; otherwise null
     * @see IMechUnit#getName()
     */
    IMechUnit getUnit(String name);

    /**
     * @return an iterable containing all registered units
     */
    Iterable<IMechUnit> getUnits();

    /**
     * @return a map containing all config values
     */
    Map<String,IConfigValue> getConfigValues();

    /**
     * This method gets a specific config value by its key, if it exists; otherwise it returns null.
     * Config values allow API implementers to easily examine and set MysticalMechanics' config values.
     *
     * @param key the key for a config value
     * @return a config value
     */
    IConfigValue getConfigValue(String key);

    CreativeTabs getCreativeTab();
}
