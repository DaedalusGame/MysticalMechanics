package mysticalmechanics;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public interface IProxy {
    void preInit();

    void playMachineSound(TileEntity tile, int id, int playId, SoundEvent soundIn, SoundCategory categoryIn, boolean repeat, float volume, float pitch, float xIn, float yIn, float zIn);

    boolean isGearHit(TileEntity tile, EnumFacing facing);

    void renderGear(ItemStack gear, ItemStack gearHologram, boolean renderHologram, float partialTicks, double offset, double scale, float angle);

    void renderAxle(ModelResourceLocation resLoc, EnumFacing.Axis axis, float angle);
}
