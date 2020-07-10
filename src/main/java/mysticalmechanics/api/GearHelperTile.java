package mysticalmechanics.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nullable;

public class GearHelperTile extends GearHelper {
    TileEntity tile;
    EnumFacing facing;

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

    @Override
    public void attach(@Nullable EntityPlayer player, ItemStack stack) {
        super.attach(player, stack);
        IGearBehavior behavior = getBehavior();
        if(behavior != null)
            behavior.onAttach(tile, facing, gear, data, player);
        tile.getWorld().playSound(null, tile.getPos(), MysticalMechanicsAPI.GEAR_ADD, SoundCategory.BLOCKS,1.0f,1.0f);
    }

    @Override
    public ItemStack detach(@Nullable EntityPlayer player) {
        ItemStack stack;
        IGearBehavior behavior = getBehavior();
        if(behavior != null)
            stack = behavior.onDetach(tile, facing, gear, data, player);
        else
            stack = getGear();
        super.detach(player);
        tile.getWorld().playSound(null, tile.getPos(), MysticalMechanicsAPI.GEAR_REMOVE, SoundCategory.BLOCKS,1.0f,1.0f);
        return stack;
    }

    public void tick(double power) {
        IGearBehavior behavior = getBehavior();
        if (behavior != null)
            behavior.tick(tile, facing, gear, data, power);
    }

    public void visualUpdate() {
        IGearBehavior behavior = getBehavior();
        if(behavior != null)
            behavior.visualUpdate(tile, facing, gear, data);
    }
}
