package mysticalmechanics.api;

import mysticalmechanics.api.lubricant.ILubricantCapability;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class MysticalMechanicsAPI {
    public static IMysticalMechanicsAPI IMPL;

    @CapabilityInject(IMechCapability.class)
    public static final Capability<IMechCapability> MECH_CAPABILITY = null;
    @CapabilityInject(ILubricantCapability.class)
    public static final Capability<ILubricantCapability> LUBRICANT_CAPABILITY = null;


    @GameRegistry.ObjectHolder("mysticalmechanics:block.gear.add")
    public static SoundEvent GEAR_ADD;
    @GameRegistry.ObjectHolder("mysticalmechanics:block.gear.remove")
    public static SoundEvent GEAR_REMOVE;
}
