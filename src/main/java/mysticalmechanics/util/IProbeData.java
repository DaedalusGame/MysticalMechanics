package mysticalmechanics.util;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.player.EntityPlayer;

public interface IProbeData {
    void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, IProbeHitData data);
}
