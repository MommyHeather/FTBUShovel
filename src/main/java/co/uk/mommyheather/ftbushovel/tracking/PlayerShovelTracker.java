package co.uk.mommyheather.ftbushovel.tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import co.uk.mommyheather.ftbushovel.net.DelayedPacketSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class PlayerShovelTracker {
    private static HashMap<UUID, PlayerShovelTracker> instances = new HashMap<>();


    public static void startTracking(EntityPlayer player) {
        if (instances.containsKey(player.getUniqueID())) return;
        PlayerShovelTracker tracker = new PlayerShovelTracker(player);
        instances.put(player.getUniqueID(), tracker);
    }

    public static void stopTracking(EntityPlayer player) {
        if (!instances.containsKey(player.getUniqueID())) return;
        instances.remove(player.getUniqueID()).destroy();
    }

    public static void trackMovement(EntityPlayer player) {
        if (!instances.containsKey(player.getUniqueID())) {
            return;
        }

        instances.get(player.getUniqueID()).update();
        
    }

    private EntityPlayer player;
    private World world;
    private int x;
    private int y;
    private int z;
    private int chunkX;
    private int chunkZ;

    private int lastChunkX;
    private int lastChunkZ;
    private int dim;
    private boolean surfaced;

    private ArrayList<TrackedChunk> chunks;

    private PlayerShovelTracker(EntityPlayer player) {
        this.player = player;
        chunks = new ArrayList<TrackedChunk>();
        player.addChatMessage(new ChatComponentText("Left click to claim, right click to unclaim."));
        player.addChatMessage(new ChatComponentText("Crouch left click to load, crouch right click to unload."));
        init();

       // makeChunk(corners::add, dim, chunkX, chunkZ, player);
    }

    private void updatePositions() {
        this.x = (int) player.posX;
        this.y = (int) player.posY;
        this.z = (int) player.posZ;
        this.world = player.worldObj;
        this.dim = player.dimension;
        this.surfaced = world.canBlockSeeTheSky(x, y, z);
        this.lastChunkX = chunkX;
        this.lastChunkZ = chunkZ;
        this.chunkX = player.chunkCoordX;
        this.chunkZ = player.chunkCoordZ;
    }

    private void init() {
        updatePositions();

        int radius = 1;

        for (int i=chunkX-radius;i<=chunkX+radius;i++) {
            for (int j=chunkZ-radius;j<=chunkZ+radius;j++) {
                TrackedChunk.makeChunk(chunks, dim, i, j, player);
            }

        }

    }

    public void update() {
        updatePositions();
        if ((this.lastChunkX != chunkX) || (this.lastChunkZ != chunkZ)) {
            reset();
           // stopTracking(player);
          //  startTracking(player);
        }
        if (surfaced) {
            for (TrackedChunk chunk : chunks) {
                chunk.update();
            }
        }
    }

    public void destroy() {
        for (TrackedChunk chunk : chunks) {
            chunk.destroy();
        }
        DelayedPacketSender.cancel((EntityPlayerMP) this.player);
        chunks.clear();
    }

    public void reset() {
        int radius = 1;

        TrackedChunk.reset(
            radius, chunkX, chunkZ, chunks, dim, player
        );
        
    }

}
