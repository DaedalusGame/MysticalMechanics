package mysticalmechanics.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class GearHelperTile extends GearHelper {
    TileEntity tile;
    EnumFacing facing;

    double angle;
    double lastAngle;

    public GearHelperTile(TileEntity tile, EnumFacing facing) {
        this.tile = tile;
        this.facing = facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public double getAngle() {
        return angle;
    }

    public double getLastAngle() {
        return lastAngle;
    }

    public double getPartialAngle(double partialTicks) {
        return MathHelper.clampedLerp(getLastAngle(),getAngle(),partialTicks);
    }

    @Override
    public void attach(@Nullable EntityPlayer player, ItemStack stack) {
        super.attach(player, stack);
        IGearBehavior behavior = getBehavior();
        behavior.onAttach(tile, getFacing(), gear, data, player);
        tile.getWorld().playSound(null, tile.getPos(), MysticalMechanicsAPI.GEAR_ADD, SoundCategory.BLOCKS,1.0f,1.0f);
    }

    @Override
    public ItemStack detach(@Nullable EntityPlayer player) {
        ItemStack stack;
        IGearBehavior behavior = getBehavior();
        stack = behavior.onDetach(tile, getFacing(), gear, data, player);
        super.detach(player);
        tile.getWorld().playSound(null, tile.getPos(), MysticalMechanicsAPI.GEAR_REMOVE, SoundCategory.BLOCKS,1.0f,1.0f);
        angle = 0;
        lastAngle = 0;
        return stack;
    }

    public void tick(double power) {
        IGearBehavior behavior = getBehavior();
        behavior.tick(tile, getFacing(), gear, data, power);
    }

    public void visualUpdate(double power) {
        IGearBehavior behavior = getBehavior();
        behavior.visualUpdate(tile, getFacing(), gear, data);
        updateAngle(power);
    }

    private void updateAngle(double power) {
        lastAngle = angle;
        angle += power;
    }
}
