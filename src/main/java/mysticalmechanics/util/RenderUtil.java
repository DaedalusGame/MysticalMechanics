package mysticalmechanics.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderUtil {
    public static int lightx = 0xF000F0;
    public static int lighty = 0xF000F0;

    /**
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param textures
     *
     * Order the textures and inversions like so: up (pos Y), down (neg Y), north (neg Z), south (pos Z), west (neg X), east (pos X)
     */
    public static void addBox(BufferBuilder b, double x1, double y1, double z1, double x2, double y2, double z2, StructUV[] textures, int[] inversions){
        //BOTTOM FACE
        b.pos(x1, y1, z1).tex(textures[0].minU,textures[0].minV).color(255, 255, 255, 255).normal(0, -1*inversions[0], 0).endVertex();
        b.pos(x1, y1, z2).tex(textures[0].maxU,textures[0].minV).color(255, 255, 255, 255).normal(0, -1*inversions[0], 0).endVertex();
        b.pos(x2, y1, z2).tex(textures[0].maxU,textures[0].maxV).color(255, 255, 255, 255).normal(0, -1*inversions[0], 0).endVertex();
        b.pos(x2, y1, z1).tex(textures[0].minU,textures[0].maxV).color(255, 255, 255, 255).normal(0, -1*inversions[0], 0).endVertex();
        //TOP FACE
        b.pos(x1, y2, z1).tex(textures[1].minU,textures[1].minV).color(255, 255, 255, 255).normal(0, 1*inversions[1], 0).endVertex();
        b.pos(x1, y2, z2).tex(textures[1].maxU,textures[1].minV).color(255, 255, 255, 255).normal(0, 1*inversions[1], 0).endVertex();
        b.pos(x2, y2, z2).tex(textures[1].maxU,textures[1].maxV).color(255, 255, 255, 255).normal(0, 1*inversions[1], 0).endVertex();
        b.pos(x2, y2, z1).tex(textures[1].minU,textures[1].maxV).color(255, 255, 255, 255).normal(0, 1*inversions[1], 0).endVertex();
        //NORTH FACE
        b.pos(x1, y1, z1).tex(textures[2].minU,textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1*inversions[2]).endVertex();
        b.pos(x2, y1, z1).tex(textures[2].maxU,textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1*inversions[2]).endVertex();
        b.pos(x2, y2, z1).tex(textures[2].maxU,textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1*inversions[2]).endVertex();
        b.pos(x1, y2, z1).tex(textures[2].minU,textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1*inversions[2]).endVertex();
        //SOUTH FACE
        b.pos(x1, y1, z2).tex(textures[3].minU,textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1*inversions[3]).endVertex();
        b.pos(x2, y1, z2).tex(textures[3].maxU,textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1*inversions[3]).endVertex();
        b.pos(x2, y2, z2).tex(textures[3].maxU,textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1*inversions[3]).endVertex();
        b.pos(x1, y2, z2).tex(textures[3].minU,textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1*inversions[3]).endVertex();
        //WEST FACE
        b.pos(x1, y1, z1).tex(textures[4].minU,textures[4].minV).color(255, 255, 255, 255).normal(-1*inversions[4], 0, 0).endVertex();
        b.pos(x1, y1, z2).tex(textures[4].maxU,textures[4].minV).color(255, 255, 255, 255).normal(-1*inversions[4], 0, 0).endVertex();
        b.pos(x1, y2, z2).tex(textures[4].maxU,textures[4].maxV).color(255, 255, 255, 255).normal(-1*inversions[4], 0, 0).endVertex();
        b.pos(x1, y2, z1).tex(textures[4].minU,textures[4].maxV).color(255, 255, 255, 255).normal(-1*inversions[4], 0, 0).endVertex();
        //EAST FACE
        b.pos(x2, y1, z1).tex(textures[5].minU,textures[5].minV).color(255, 255, 255, 255).normal(1*inversions[5], 0, 0).endVertex();
        b.pos(x2, y1, z2).tex(textures[5].maxU,textures[5].minV).color(255, 255, 255, 255).normal(1*inversions[5], 0, 0).endVertex();
        b.pos(x2, y2, z2).tex(textures[5].maxU,textures[5].maxV).color(255, 255, 255, 255).normal(1*inversions[5], 0, 0).endVertex();
        b.pos(x2, y2, z1).tex(textures[5].minU,textures[5].maxV).color(255, 255, 255, 255).normal(1*inversions[5], 0, 0).endVertex();
    }

    public static boolean renderItemStack(Minecraft mc, RenderItem itemRender, ItemStack itm, int x, int y, String txt) {
        GlStateManager.color(1F, 1F, 1F);

        boolean rc = false;
        if (!itm.isEmpty() && itm.getItem() != null) {
            rc = true;
            GlStateManager.pushMatrix();

            GlStateManager.translate(x, y, 32.0F);
            GlStateManager.scale(0.5f,0.5f,0.5f);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
            itemRender.renderItemAndEffectIntoGUI(itm, 0, 0);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, 0, 0, txt);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
        }

        return rc;
    }

    public static int renderText(Minecraft mc, int x, int y, String txt) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableLighting();
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        int width = mc.fontRenderer.getStringWidth(txt);
        mc.fontRenderer.drawStringWithShadow(txt, x, y, 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager.enableBlend();


        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        return width;
    }
}
