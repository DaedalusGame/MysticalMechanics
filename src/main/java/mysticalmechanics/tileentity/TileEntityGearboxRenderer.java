package mysticalmechanics.tileentity;

import mysticalmechanics.api.GearHelperTile;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockGearbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class TileEntityGearboxRenderer extends TileEntitySpecialRenderer<TileEntityGearbox> {
	static int tick;

	public TileEntityGearboxRenderer(){
		super();
		MinecraftForge.EVENT_BUS.register(getClass());
	}

    @SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			tick++;
	}

    @Override
    public void render(TileEntityGearbox tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha){
        if (tile != null){
			IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() instanceof BlockGearbox){
            	EntityPlayer player = Minecraft.getMinecraft().player;
				ItemStack gearHologram = player.getHeldItemMainhand();

				int face = 0;
				for (GearHelperTile gear : tile.gears) {
					EnumFacing direction = EnumFacing.VALUES[face];
					boolean sideHit = MysticalMechanicsAPI.IMPL.isGearHit(tile, direction);
					boolean renderHologram = MysticalMechanicsAPI.IMPL.shouldRenderHologram(gearHologram, !gear.isEmpty(), sideHit, tile.canAttachGear(direction, gearHologram));
					if (!gear.isEmpty() || renderHologram) {
						GlStateManager.pushMatrix();
						GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
						
						//rotate gear model items to fit the sides of the gearbox.
						switch (direction) {
							case DOWN:
								GlStateManager.rotate(-90, 1, 0, 0);
								break;
							case UP:
								GlStateManager.rotate(90, 1, 0, 0);
								break;
							case NORTH:
								break;
							case WEST:
								GlStateManager.rotate(90, 0, 1, 0);
								break;
							case SOUTH:
								GlStateManager.rotate(180, 0, 1, 0);
								break;
							case EAST:
								GlStateManager.rotate(270, 0, 1, 0);
								break;
							default:
								break;
						}

						MysticalMechanicsAPI.IMPL.renderGear(gear.getGear(), gearHologram, renderHologram, partialTicks, -0.375, 0.875, (float) gear.getPartialAngle(partialTicks));

						GlStateManager.popMatrix();
						GlStateManager.disableBlend();
						
					}
					//this keeps track of the current side being rendered.
					face++;					
				} 
            }

        }
    }
}
