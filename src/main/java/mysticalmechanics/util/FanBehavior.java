package mysticalmechanics.util;

import mysticalmechanics.api.IGearBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class FanBehavior implements IGearBehavior {
    @Override
    public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        return 0;
    }

    @Override
    public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        return power;
    }

    @Override
    public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
        //NOOP
    }

    @Override
    public boolean canTick(ItemStack gear) {
        return true;
    }

    @Override
    public void tick(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
        World world = tile.getWorld();
        AxisAlignedBB aabb = new AxisAlignedBB(tile.getPos().offset(facing));
        double distance = getBlowDistance(power);
        double vx = facing.getFrontOffsetX();
        double vy = facing.getFrontOffsetY();
        double vz = facing.getFrontOffsetZ();
        double blowVelocity = getBlowVelocity(power);
        aabb = aabb.expand(vx *distance, vy *distance, vz *distance);

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,aabb);
        for(Entity entity : entities) {
            entity.addVelocity(vx*blowVelocity,vy*blowVelocity,vz*blowVelocity);
        }
    }

    private double getBlowVelocity(double power) {
        return power / 800.0;
    }

    private double getBlowDistance(double power) {
        return Math.sqrt(power) / 3.0;
    }
}
