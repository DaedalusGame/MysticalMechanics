package betterwithmods.api.util;

import net.minecraft.item.ItemStack;

public interface IWood {

    ItemStack getLog(int count);

    ItemStack getPlank(int count);

    ItemStack getBark(int count);

    ItemStack getSawdust(int count);

}
