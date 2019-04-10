package mysticalmechanics.tileentity;

import mysticalmechanics.block.BlockAxle;
import mysticalmechanics.util.RenderUtil;
import mysticalmechanics.util.StructUV;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityAxleRenderer extends TileEntitySpecialRenderer<TileEntityAxle> {
    public int lightx = 0, lighty = 0;

    StructUV[] textures = new StructUV[]{
            new StructUV(7,0,9,2,16,16),
            new StructUV(7,0,9,2,16,16),
            new StructUV(7,0,9,16,16,16),
            new StructUV(7,0,9,16,16,16),
            new StructUV(7,0,9,16,16,16),
            new StructUV(7,0,9,16,16,16)
    };

    public TileEntityAxleRenderer(){
        super();
    }

    @Override
    public void render(TileEntityAxle tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha){
    	if (tile != null){
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() instanceof BlockAxle){
                
            	EnumFacing.Axis axis = state.getValue(BlockAxle.facing).getAxis();
            	EnumFacing facing = state.getValue(BlockAxle.facing);
                ResourceLocation texture = new ResourceLocation(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state).getIconName());
                texture = new ResourceLocation(texture.getResourceDomain(),"textures/"+texture.getResourcePath()+".png");

                Minecraft.getMinecraft().renderEngine.bindTexture(texture);
                GlStateManager.disableCull();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buffer = tess.getBuffer();

                GlStateManager.pushMatrix();
                GlStateManager.translate(x+0.5, y+0.5, z+0.5);
                if (axis == EnumFacing.Axis.Y){
                    GL11.glRotated(180, 1, 0, 0);
                }

                if (axis == EnumFacing.Axis.Z){
                    GL11.glRotated(90, 1, 0, 0);
                }

                if (axis == EnumFacing.Axis.X){
                    GL11.glRotated(90, 0, 1, 0);
                    GL11.glRotated(90, 1, 0, 0);
                }
                
                TileEntityAxle frontAxle = tile.getConnectionTile(tile.getPos().offset(facing),facing);
                TileEntityAxle backAxle = tile.getConnectionTile(tile.getPos().offset(facing.getOpposite()), facing.getOpposite());              
                
                if(frontAxle != null) {
                	 tile.angle = frontAxle.angle;
                     tile.lastAngle = frontAxle.lastAngle;
                }else if(backAxle != null) {
                	tile.angle = backAxle.angle;
                   tile.lastAngle = backAxle.lastAngle;
                }
                double angle = tile.angle;
                double lastAngle = tile.lastAngle;
                    
                
                GlStateManager.rotate((float)(partialTicks * angle)+(1 - partialTicks)*(float)lastAngle, 0, 1, 0);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                RenderUtil.addBox(buffer, -0.09375, -0.5, -0.09375, 0.09375, 0.5, 0.09375, textures, new int[]{1,1,1,1,1,1});
                tess.draw();
                GlStateManager.popMatrix();
            }

        }
    }
}
