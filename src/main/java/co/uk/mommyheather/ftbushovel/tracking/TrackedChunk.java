package co.uk.mommyheather.ftbushovel.tracking;
import java.util.ArrayList;

import co.uk.mommyheather.ftbushovel.FTBUShovel;
import co.uk.mommyheather.ftbushovel.net.DelayedPacketSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TrackedChunk {
    
        //Stores four corners. This is done to minimise packets sent when moving about, reducing client lag.
        private int x;
        private int z;

        private TrackedCorner corner1;
        private TrackedCorner corner2;
        private TrackedCorner corner3;
        private TrackedCorner corner4;

        public TrackedChunk(int x, int z, TrackedCorner corner1, TrackedCorner corner2, TrackedCorner corner3,
                TrackedCorner corner4) {
            this.x = x;
            this.z = z;
            this.corner1 = corner1;
            this.corner2 = corner2;
            this.corner3 = corner3;
            this.corner4 = corner4;
        }

        public void update() {
            corner1.update();
            corner2.update();
            corner3.update();
            corner4.update();
        }

        public void destroy() {
            corner1.destroy();
            corner2.destroy();
            corner3.destroy();
            corner4.destroy();
        }

        public boolean checkLocation(int xIn, int zIn) {
            if (xIn == x) {
                if (zIn == z) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            TrackedChunk chunk = (TrackedChunk) o;
            return chunk.checkLocation(x, z);
        }

        public static void makeChunk(ArrayList<TrackedChunk> list, int dim, int chunkX, int chunkZ, EntityPlayer player) {
            for (TrackedChunk chunk : list) {
                if (chunk.x == chunkX) {
                    if (chunk.z == chunkZ) {
                        return;
                    }
                }
            }
            World world = player.worldObj;
            //Four corners
            int chunkU1 = chunkX * 16;
            int chunkV1 = chunkZ * 16;
            int chunkU2 = chunkU1 + 15;
            int chunkV2 = chunkV1 + 15;
    
            TrackedCorner corner1;
            TrackedCorner corner2;
            TrackedCorner corner3;
            TrackedCorner corner4;
    
            int markerY;
    
            int indicator1X;
            int indicator1Y;
            int indicator1Z;
            int indicator2X;
            int indicator2Y;
            int indicator2Z;
    
            //First corner
            markerY = world.getHeightValue(chunkU1, chunkV1);
            indicator1X = chunkU1;
            indicator1Z = chunkV1 + 1;
            indicator2X = chunkU1 + 1;
            indicator2Z = chunkV1;
    
            indicator1Y = world.getHeightValue(indicator1X, indicator1Z);
            indicator2Y = world.getHeightValue(indicator2X, indicator2Z);
    
            corner1 = new TrackedCorner(chunkU1, Math.max(markerY, Math.max(indicator1Y, indicator2Y)), chunkV1, indicator1X, indicator1Z, indicator2X, 
            indicator2Z, player, chunkX, chunkZ);
    
            //now the others
            markerY = world.getHeightValue(chunkU1, chunkV2);
            indicator1X = chunkU1;
            indicator1Z = chunkV2 - 1;
            indicator2X = chunkU1 + 1;
            indicator2Z = chunkV2;
    
            indicator1Y = world.getHeightValue(indicator1X, indicator1Z);
            indicator2Y = world.getHeightValue(indicator2X, indicator2Z);
    
            corner2 = new TrackedCorner(chunkU1, Math.max(markerY, Math.max(indicator1Y, indicator2Y)), chunkV2, indicator1X, indicator1Z, indicator2X, 
            indicator2Z, player, chunkX, chunkZ);

            markerY = world.getHeightValue(chunkU2, chunkV1);
            indicator1X = chunkU2;
            indicator1Z = chunkV1 + 1;
            indicator2X = chunkU2 - 1;
            indicator2Z = chunkV1;
    
            indicator1Y = world.getHeightValue(indicator1X, indicator1Z);
            indicator2Y = world.getHeightValue(indicator2X, indicator2Z);
    
            corner3 = new TrackedCorner(chunkU2, Math.max(markerY, Math.max(indicator1Y, indicator2Y)), chunkV1, indicator1X, indicator1Z, indicator2X, 
            indicator2Z, player, chunkX, chunkZ);
            
            
            markerY = world.getHeightValue(chunkU2, chunkV2);
            indicator1X = chunkU2;
            indicator1Z = chunkV2 - 1;
            indicator2X = chunkU2 - 1;
            indicator2Z = chunkV2;
    
            indicator1Y = world.getHeightValue(indicator1X, indicator1Z);
            indicator2Y = world.getHeightValue(indicator2X, indicator2Z);
    
            corner4 = new TrackedCorner(chunkU2, Math.max(markerY, Math.max(indicator1Y, indicator2Y)), chunkV2, indicator1X, indicator1Z, indicator2X, 
            indicator2Z, player, chunkX, chunkZ);

            TrackedChunk chunk = new TrackedChunk(chunkX, chunkZ, corner1, corner2, corner3, corner4);

            list.add(chunk);
    
    
        }

        public static void reset(int radius, int chunkX, int chunkZ, ArrayList<TrackedChunk> chunks, int dim, EntityPlayer player) {
            DelayedPacketSender.cancel((EntityPlayerMP) player);
            ArrayList<TrackedChunk> tempChunks = new ArrayList<>(chunks);

            for (int i=chunkX-radius;i<=chunkX+radius;i++) {
                for (int j=chunkZ-radius;j<=chunkZ+radius;j++) {
                    TrackedChunk.makeChunk(chunks, dim, i, j, player);
                }
    
            }

            
        }
}
