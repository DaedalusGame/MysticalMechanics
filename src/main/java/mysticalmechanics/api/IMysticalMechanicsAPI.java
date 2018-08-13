package mysticalmechanics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

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
}
