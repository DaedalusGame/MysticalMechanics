package mysticalmechanics;

import mysticalmechanics.tileentity.TileEntityAxle;
import mysticalmechanics.tileentity.TileEntityAxleRenderer;
import mysticalmechanics.tileentity.TileEntityGearbox;
import mysticalmechanics.tileentity.TileEntityGearboxRenderer;
import mysticalmechanics.util.MachineSound;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAxle.class, new TileEntityAxleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGearbox.class, new TileEntityGearboxRenderer());
    }

    @Override
    public void playMachineSound(TileEntity tile, int id, SoundEvent soundIn, SoundCategory categoryIn, boolean repeat, float volume, float pitch, float xIn, float yIn, float zIn) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new MachineSound(tile,id,soundIn,categoryIn,repeat,volume,pitch,xIn,yIn,zIn));
    }
}
