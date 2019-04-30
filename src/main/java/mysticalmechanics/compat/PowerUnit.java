package mysticalmechanics.compat;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mysticalmechanics.api.IMechUnit;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class PowerUnit implements IElement {
    public double power;
    public boolean input;
    public boolean output;
    public ItemStack gear;
    public EnumFacing facing;

    public PowerUnit(double power, boolean input, boolean output, ItemStack gear, EnumFacing facing) {
        this.power = power;
        this.input = input;
        this.output = output;
        this.gear = gear;
        this.facing = facing;
    }

    @SideOnly(Side.CLIENT)
    private String format() {
        IMechUnit unit = MysticalMechanicsAPI.IMPL.getDefaultUnit();
        if(input == output)
            return I18n.format("mysticalmechanics.probe.power",unit.format(power));
        else if(input)
            return I18n.format("mysticalmechanics.probe.input_power",unit.format(power));
        else
            return I18n.format("mysticalmechanics.probe.output_power",unit.format(power));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(int x, int y) {
        String text = format();
        if(!gear.isEmpty()) {
            RenderUtil.renderItemStack(Minecraft.getMinecraft(), Minecraft.getMinecraft().getRenderItem(), gear, x, y, facing.getName().toUpperCase().substring(0,1));
        }
        RenderUtil.renderText(Minecraft.getMinecraft(),x+10,y, text);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getWidth() {
        String text = format();
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text)+10;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getHeight() {
        return 10;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer wrapper = new PacketBuffer(buf);
        wrapper.writeDouble(power);
        wrapper.writeBoolean(input);
        wrapper.writeBoolean(output);
        wrapper.writeItemStack(gear);
        wrapper.writeEnumValue(facing);
    }

    @Override
    public int getID() {
        return TheOneProbe.ELEMENT_POWERUNIT;
    }

    public static class Factory implements IElementFactory {
        @Override
        public IElement createElement(ByteBuf buf) {
            PacketBuffer wrapper = new PacketBuffer(buf);
            double power = wrapper.readDouble();
            boolean input = wrapper.readBoolean();
            boolean output = wrapper.readBoolean();
            ItemStack gear = null;
            try {
                gear = wrapper.readItemStack();
            } catch (IOException e) {
                e.printStackTrace();
            }
            EnumFacing facing = wrapper.readEnumValue(EnumFacing.class);
            return new PowerUnit(power,input,output,gear,facing);
        }
    }
}
