package mysticalmechanics.tileentity;

import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.block.BlockAxle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public class TileEntityAxleRenderer extends TileEntitySpecialRenderer<TileEntityAxle> {
    public TileEntityAxleRenderer(){
        super();
    }

    @Override
    public void render(TileEntityAxle tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        Block block = state.getBlock();
        if (block instanceof BlockAxle){
            EnumFacing.Axis axis = tile.getAxis();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x+0.5, y+0.5, z+0.5);

            MysticalMechanicsAPI.IMPL.syncAngle(tile, tile.getBackward());

            double angle = tile.angle;
            double lastAngle = tile.lastAngle;

            MysticalMechanicsAPI.IMPL.renderAxle(new ModelResourceLocation(block.getRegistryName(), "normal"), axis, (float)(partialTicks * angle)+(1 - partialTicks)*(float)lastAngle);

            GlStateManager.popMatrix();
        }

    }
}
