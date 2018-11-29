package mysticalmechanics.util;

import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Misc {
    static double epsilion = 0.00001;

    public static boolean isRoughlyEqual(double a, double b) {
        return a == b || Math.abs(a - b) < epsilion;
    }

    public static void syncTE(TileEntity tile) {
        World world = tile.getWorld();
        if(world instanceof WorldServer) {
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if (packet != null) {
                PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
                int i = tile.getPos().getX() >> 4;
                int j = tile.getPos().getZ() >> 4;
                PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);
                if(entry != null)
                    entry.sendPacket(packet);
            }
        }
    }
}
