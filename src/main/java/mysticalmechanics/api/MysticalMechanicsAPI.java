package mysticalmechanics.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MysticalMechanicsAPI {
    public static IMysticalMechanicsAPI IMPL;

    @CapabilityInject(IMechCapability.class)
    public static final Capability<IMechCapability> MECH_CAPABILITY = null;
}
