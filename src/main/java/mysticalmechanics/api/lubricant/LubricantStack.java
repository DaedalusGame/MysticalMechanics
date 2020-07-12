package mysticalmechanics.api.lubricant;

import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.nbt.NBTTagCompound;

public class LubricantStack {
    private ILubricant lubricant;
    private int amount;

    public LubricantStack(ILubricant lubricant, int amount) {
        this.lubricant = lubricant;
        this.amount = amount;
    }

    public LubricantStack(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public ILubricant getLubricant() {
        return lubricant;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }

    public void increment(int n) {
        amount += n;
    }

    public void deplete(int n) {
        amount -= n;
    }

    NBTTagCompound writeToNBT(NBTTagCompound tag) {
        lubricant.writeToNBT(tag);
        tag.setInteger("amount", amount);
        return tag;
    }

    void readFromNBT(NBTTagCompound tag) {
        lubricant = MysticalMechanicsAPI.IMPL.deserializeLubricant(tag);
        amount = tag.getInteger("amount");
    }

    public NBTTagCompound serializeNBT() {
        return writeToNBT(new NBTTagCompound());
    }

    public String getUnlocalizedName() {
        return lubricant.getUnlocalizedName();
    }
}
