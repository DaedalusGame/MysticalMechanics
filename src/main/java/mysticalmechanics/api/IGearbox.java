package mysticalmechanics.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IGearbox {
    /**
     * @param facing the side to attach the gear to.
     * @param stack the gear to attach.
     */
    void attachGear(EnumFacing facing, ItemStack stack);

    /**
     * @param facing the side to remove the gear from.
     * @return the removed gear. Does not need to be the same gear as previously attached, but could be a damaged version or an empty stack.
     */
    ItemStack detachGear(EnumFacing facing);

    /**
     * @param facing the side to check for.
     * @return the currently attached gear on this face, or ItemStack.EMPTY if none.
     */
    ItemStack getGear(EnumFacing facing);

    /**
     * @param facing the side to check for.
     * @param stack an ItemStack representing a gear.
     * @return whether the specified stack can be attached on this face as a gear.
     */
    boolean canAttachGear(EnumFacing facing, ItemStack stack);

    /**
     * Use/Implement this method for the number of connected outputs this gearbox has. Every connected output should only receive power divided by this value to prevent power loops.
     *
     * @return the number of directly connected mechanical blocks on output faces.
     */
    int getConnections();
}
