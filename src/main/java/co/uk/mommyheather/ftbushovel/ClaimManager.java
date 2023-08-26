package co.uk.mommyheather.ftbushovel;

import ftb.lib.LMDimUtils;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.net.MessageAreaUpdate;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ChunkType;
import ftb.utils.world.claims.ClaimedChunk;
import ftb.utils.world.ranks.RankConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ClaimManager {


    public static void handleInteract(PlayerInteractEvent event, EntityPlayer player) {
        //TODO : adjust this to be configurable
        event.setCanceled(true);
        LMPlayerServer p = LMWorldServer.inst.getPlayer(player);
        RankConfig c = p.getRank().config;
        int dimension = player.dimension;
        int chunkX = event.x / 16;
        int chunkZ = event.z / 16;
        switch (event.action) {
            case LEFT_CLICK_BLOCK:
                if (!player.isSneaking()) {
                    player.addChatMessage(new ChatComponentText(handleClaim(p, dimension, chunkX, chunkZ, c)));
                }
                else {
                    player.addChatMessage(new ChatComponentText(handleLoad(p, dimension, chunkX, chunkZ, c, true)));
                }
                return;
            case RIGHT_CLICK_BLOCK:
                if (!player.isSneaking()) {
                    player.addChatMessage(new ChatComponentText(handleUnClaim(p, dimension, chunkX, chunkZ, c)));
                }
                else {
                    player.addChatMessage(new ChatComponentText(handleLoad(p, dimension, chunkX, chunkZ, c, false)));
                }
                return;
            default:
                return;
        }
    }

    
    public static String handleClaim(LMPlayerServer player, int dim, int x, int z, RankConfig config) {
        if(config.dimension_blacklist.get().contains(dim)) return "The current dimension does not allow claims!";
        int prev = player.getClaimedChunks();
        int max = config.max_claims.getAsInt();
        if(prev >= max) return "You have reached your max claim limit! " + prev + "/" + max;
        ChunkType t = LMWorldServer.inst.claimedChunks.getType(dim, x, z);

        if (!t.isClaimed() && !t.isChunkOwner(player)) {
            return "Someone else has claimed this chunk!";
        }
        
        if(!t.isClaimed() && t.isChunkOwner(player) && LMWorldServer.inst.claimedChunks.put(new ClaimedChunk(player.getPlayerID(), dim, x, z)))
        player.sendUpdate();
        if (player.getClaimedChunks() == prev) return "You have already claimed this chunk!";

        return "Claim complete! You have claimed " + player.getClaimedChunks() + "/" + max + " chunks claimed.";
    }

    public static String handleUnClaim(LMPlayerServer player, int dim, int x, int z, RankConfig config) {
        
        ChunkType t = LMWorldServer.inst.claimedChunks.getType(dim, x, z);
        if (t.isChunkOwner(player)) {
            int prev = player.getClaimedChunks();
            int max = config.max_claims.getAsInt();
            LMWorldServer.inst.claimedChunks.remove(dim, x, z);
            player.sendUpdate();
            if (player.getClaimedChunks() == prev) return "This chunk is not claimed!";
            return "Chunk unclaimed! You have " + player.getClaimedChunks() + "/" + max + " chunks claimed."; 
        }

        return "You do not own this chunk, so you cannot unclaim it.";
    }

    public static String handleLoad(LMPlayerServer player, int dim, int x, int z, RankConfig config, boolean flag) {
		ClaimedChunk chunk = LMWorldServer.inst.claimedChunks.getChunk(dim, x, z);
		if(chunk == null) return "This chunk is not claimed!";
        int prev = player.getLoadedChunks(false);
        int max = config.max_loaded_chunks.getAsInt();
		
		if(flag != chunk.isChunkloaded && player.equalsPlayer(chunk.getOwnerS()))
		{
			if(flag)
			{
				if(config.dimension_blacklist.get().contains(dim)) return "This dimension is blacklisted!";
				if(prev >= max) return "You're over the limit of loadable chunks! " + player.getLoadedChunks(false) + "/" + max;
			}
			
			chunk.isChunkloaded = flag;
			FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(dim));
			
			if(player.getPlayer() != null) new MessageAreaUpdate(player, x, z, dim, 1, 1).sendTo(player.getPlayer());
			player.sendUpdate();

            String t = flag ? "loaded! " : "unloaded! ";

            return "Chunk successfully " + t + player.getLoadedChunks(false) + "/" + max;
		}
        return "Nothing changed - chunk already matched requested state.";
    }
}
