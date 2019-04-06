package mysticalmechanics.util;

import net.minecraft.network.play.server.SPacketBlockChange;
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

	public static void syncTE(TileEntity tile, boolean broken) {
		World world = tile.getWorld();		
		if (world instanceof WorldServer) {
			PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
			SPacketUpdateTileEntity packet = tile.getUpdatePacket();
			if (packet != null) {
				int i = tile.getPos().getX() >> 4;
				int j = tile.getPos().getZ() >> 4;
				PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);				
				if (entry != null) {
					if(broken){
						//tells the client the block has changed if it has been broken
						entry.sendPacket(new SPacketBlockChange(chunkMap.getWorldServer(),tile.getPos()));	
					}								 
					entry.sendPacket(packet);
				}
			}

		}
	}
}
