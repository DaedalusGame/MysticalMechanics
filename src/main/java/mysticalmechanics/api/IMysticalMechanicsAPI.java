package mysticalmechanics.api;

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

    void registerLubricant(ResourceLocation resLoc, Function<NBTTagCompound, ILubricant> generator);

    void registerSimpleLubricant(SimpleLubricant lubricant);

    void unregisterLubricant(ResourceLocation resLoc);

    ILubricant deserializeLubricant(NBTTagCompound tag);

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

    void pushPower(TileEntity tileSelf, EnumFacing sideSelf, IMechCapability capSelf, boolean hasGear);

    void pullPower(TileEntity tileSelf, EnumFacing sideSelf, IMechCapability capSelf, boolean hasGear);

    /**
     *
     * @param tile The tile to check
     * @param facing The side to check
     * @return Whether the player is currently targeting this side. If the player is sneaking, this returns whether the opposite side is targeted.
     */
    boolean isGearHit(TileEntity tile, EnumFacing facing);

    /**
     *
     *
     * @param gear The gear held by the player
     * @param hasGear Whether this side has a gear attached
     * @param sideHit output from {@link #isGearHit(TileEntity, EnumFacing)}
     * @param canAttach Whether we can attach the gear on this side
     * @return Whether the gear hologram should render
     */
    boolean shouldRenderHologram(ItemStack gear, boolean hasGear, boolean sideHit, boolean canAttach);

    /**
     * Renders a gear + the hologram effect seen when hovering over gear faces
     * Expects to already be translated to xyz + 0.5;
     * Expects to already be oriented;
     *
     * @param gear the physically attached gear we should render
     * @param gearHologram the gear hologram we should render
     * @param renderHologram output from {@link #shouldRenderHologram(ItemStack, boolean, boolean, boolean)}
     * @param partialTicks
     * @param offset offset from the center (default in mystmech is -0.375)
     * @param scale size modifier (default in mystmech is 0.875)
     * @param angle angle of the gear
     */
    void renderGear(ItemStack gear, ItemStack gearHologram, boolean renderHologram, float partialTicks, double offset, double scale, float angle);

    /**
     * Renders a model as an axle (it will be rotated to face in a certain axis direction and rotate around this axis)
     * Expects to already be translated to xyz + 0.5;
     *
     * @param resLoc The model to render
     * @param axis The axis of the axle
     * @param angle The angle of the axle
     */
    void renderAxle(ModelResourceLocation resLoc, EnumFacing.Axis axis, float angle);

    /**
     * You can call this (preferably in rendering) to sync rotations. Axles use this to ensure they're all aligned.
     *
     * @param tile The tile that will sync its rotation. Must implement IHasRotation or nothing will happen.
     * @param checkDirection The face in which to check for adjacent rotating neighbors.
     */
    void syncAngle(TileEntity tile, EnumFacing checkDirection);
}
