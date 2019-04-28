package mysticalmechanics.tileentity;

import mysticalmechanics.block.BlockGearbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileEntityGearboxRenderer extends TileEntitySpecialRenderer<TileEntityGearbox> {
    public TileEntityGearboxRenderer(){
        super();
    }

    @Override
    public void render(TileEntityGearbox tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha){
        if (tile != null){
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() instanceof BlockGearbox){
				int face = 0;
				for (ItemStack gear:tile.gears) {					
					if (!gear.isEmpty()) {								

						EnumFacing direction = EnumFacing.VALUES[face];
						//double powerRatio = tile.capability.getPower(direction);						

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
						
						double angle = tile.getAngle(direction);
		                double lastAngle = tile.getLastAngle(direction);
						
						//render the gears and rotate them based on how much power they have.
						GlStateManager.translate(0, 0, -0.375);
						GlStateManager.scale(0.875, 0.875, 0.875);
						GlStateManager.rotate(
								((float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle), 0, 0, 1);									
								
						Minecraft.getMinecraft().getRenderItem().renderItem(gear,
								ItemCameraTransforms.TransformType.FIXED);
						GlStateManager.popMatrix();
						
					}
					//this keeps track of the current side being rendered.
					face++;					
				} 
            }

        }
    }
}
