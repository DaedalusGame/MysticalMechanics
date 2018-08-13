package mysticalmechanics;

import mysticalmechanics.tileentity.TileEntityAxle;
import mysticalmechanics.tileentity.TileEntityAxleRenderer;
import mysticalmechanics.tileentity.TileEntityGearbox;
import mysticalmechanics.tileentity.TileEntityGearboxRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAxle.class, new TileEntityAxleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGearbox.class, new TileEntityGearboxRenderer());
    }
}
