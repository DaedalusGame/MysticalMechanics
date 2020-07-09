package mysticalmechanics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GearHelper {
    ItemStack gear = ItemStack.EMPTY;
    IGearData data;

    public ItemStack getGear() {
        return gear;
    }

    public IGearData getData() {
        return data;
    }

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

    public void attach(ItemStack stack) {
        gear = stack;
        createData();
    }

    public ItemStack detach() {
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
        createData();
        if(data != null)
            data.readFromNBT(tag.getCompoundTag("data"));
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setTag("gear", gear.serializeNBT());
        if(data != null) {
            NBTTagCompound dataTag = new NBTTagCompound();
            data.writeToNBT(dataTag);
            tag.setTag("data", dataTag);
        }
    }
}
