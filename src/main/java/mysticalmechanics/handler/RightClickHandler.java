package mysticalmechanics.handler;

import mysticalmechanics.api.IGearbox;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RightClickHandler {
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing side = event.getFace();
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IGearbox) {
            IGearbox gearbox = (IGearbox) tile;
            ItemStack stack = event.getItemStack();
            if (gearbox.canAttachGear(side) && MysticalMechanicsAPI.IMPL.isValidGear(stack)) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }
}
