package mysticalmechanics.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class GearHelper {
    ItemStack gear = ItemStack.EMPTY;
    IGearData data;

    public void setGear(ItemStack stack) {
        gear = stack;
        data = null;
        createData();
    }

    public ItemStack getGear() {
        return gear;
    }

    public IGearData getData() {
        return data;
    }

    @Nullable
    public IGearBehavior getBehavior() {
        return MysticalMechanicsAPI.IMPL.getGearBehavior(gear);
    }

    public boolean isEmpty() {
        return gear.isEmpty();
    }

    private void createData() {
        IGearBehavior behavior = getBehavior();
        if(behavior != null)
            data = behavior.createData();
    }

    public void attach(@Nullable EntityPlayer player, ItemStack stack) {
        gear = stack;
        createData();
    }

    public ItemStack detach(@Nullable EntityPlayer player) {
        ItemStack removed = gear;
        gear = ItemStack.EMPTY;
        data = null;
        return removed;
    }

    public boolean canAttach(ItemStack stack) {
        return true;
    }

    public boolean canDetach() {
        return true;
    }

    public void readFromNBT(NBTTagCompound tag) {
        gear = new ItemStack(tag.getCompoundTag("gear"));
        data = null;
        createData();
        if(data != null)
            data.readFromNBT(tag.getCompoundTag("data"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("gear", gear.serializeNBT());
        if(data != null)
            tag.setTag("data", data.writeToNBT(new NBTTagCompound()));
        return tag;
    }
}
