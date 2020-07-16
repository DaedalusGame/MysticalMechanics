package mysticalmechanics;

import mysticalmechanics.util.MachineSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
    }

    @Override
    public void playMachineSound(TileEntity tile, int id, int playId, SoundEvent soundIn, SoundCategory categoryIn, boolean repeat, float volume, float pitch, float xIn, float yIn, float zIn) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new MachineSound(tile,id,playId,soundIn,categoryIn,repeat,volume,pitch,xIn,yIn,zIn));
    }

    @Override
    public boolean isGearHit(TileEntity tile, EnumFacing facing) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        boolean correctHit = result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.getBlockPos().equals(tile.getPos());
        if(correctHit) {
            EnumFacing sideHit = result.sideHit;
            if (player.isSneaking())
                sideHit = sideHit.getOpposite();
            return sideHit == facing;
        }
        return false;
    }

    @Override
    public void renderGear(ItemStack gear, ItemStack gearHologram, boolean renderHologram, float totalTick, double offset, double scale, float angle) {
        //render hologram
        if (gearHologram != null && renderHologram && MysticalMechanics.RENDER_GEAR_HOLOGRAM) {
            double startOffset;
            double endOffset;
            if (gear.isEmpty()) {
                startOffset = -0.5;
                endOffset = offset;
                gear = gearHologram;
            } else {
                startOffset = offset;
                endOffset = -0.5;
            }
            offset = MathHelper.clampedLerp(startOffset,endOffset,(totalTick / 10) % 1);
        }

        //GlStateManager.color(0.5f,0.5f,0.5f,0.5f);
        GlStateManager.translate(0, 0, offset);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(angle, 0, 0, 1);
        Minecraft.getMinecraft().getRenderItem().renderItem(gear, ItemCameraTransforms.TransformType.FIXED);
    }

    @Override
    public void renderAxle(ModelResourceLocation resLoc, EnumFacing.Axis axis, float angle) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = modelmanager.getModel(resLoc);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        switch (axis) {

            case X:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.rotate(90, 1, 0, 0);
                break;
            case Y:
                GlStateManager.rotate(180, 1, 0, 0);
                break;
            case Z:
                GlStateManager.rotate(90, 1, 0, 0);
                break;
        }

        GlStateManager.rotate(angle, 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
    }

}
