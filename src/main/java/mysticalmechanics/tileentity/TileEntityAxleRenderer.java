package mysticalmechanics.tileentity;

import mysticalmechanics.api.IHasRotation;
import mysticalmechanics.block.BlockAxle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

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
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "normal"));

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            EnumFacing.Axis axis = tile.getAxis();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x+0.5, y+0.5, z+0.5);
            if (axis == EnumFacing.Axis.Y){
                GlStateManager.rotate(180, 1, 0, 0);
            }

            if (axis == EnumFacing.Axis.Z){
                GlStateManager.rotate(90, 1, 0, 0);
            }

            if (axis == EnumFacing.Axis.X){
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.rotate(90, 1, 0, 0);
            }

            syncAngle(tile, tile.getBackward());

            double angle = tile.angle;
            double lastAngle = tile.lastAngle;


            GlStateManager.rotate((float)(partialTicks * angle)+(1 - partialTicks)*(float)lastAngle, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.popMatrix();
        }

    }

    private void syncAngle(TileEntityAxle tile, EnumFacing checkDirection) {
        BlockPos axlePos = tile.getPos().offset(checkDirection);
        TileEntity axleTile = tile.getWorld().getTileEntity(axlePos);
        if(axleTile instanceof IHasRotation) {
            IHasRotation axle = (IHasRotation) axleTile;
            if(axle.hasRotation(checkDirection.getOpposite())) {
                tile.angle = axle.getAngle(checkDirection.getOpposite());
                tile.lastAngle = axle.getLastAngle(checkDirection.getOpposite());
            }
        }
    }
}
