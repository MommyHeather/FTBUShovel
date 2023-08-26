package co.uk.mommyheather.ftbushovel.tracking;
import co.uk.mommyheather.ftbushovel.ClaimStateEnum;
import co.uk.mommyheather.ftbushovel.config.ConfiguredBlocks;
import co.uk.mommyheather.ftbushovel.net.DelayedPacketSender;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class TrackedCorner {
    //each corner consists of three blocks - the marker, glowstone by default, and two indicators, changing depending on if it's claimed, loaded, and who by.
    
    private int markerPosX;
    private int markerPosZ;
    private int markerPosY;

    private int indicator1PosX;
    private int indicator1PosZ;

    private int indicator2PosX;
    private int indicator2PosZ;

    private int dim;
    private int chunkX;
    private int chunkZ;
    
    private EntityPlayerMP playerMP;

    private int ticks;
    
    private ClaimStateEnum state;

    
    public TrackedCorner(int markerPosX, int markerPosY, int markerPosZ, int indicator1PosX,
            int indicator1PosZ, int indicator2PosX, int indicator2PosZ, EntityPlayer player, int chunkX, int chunkZ) {
        this.markerPosX = markerPosX;
        this.markerPosZ = markerPosZ;
        this.markerPosY = markerPosY;
        this.indicator1PosX = indicator1PosX;
        this.indicator1PosZ = indicator1PosZ;
        this.indicator2PosX = indicator2PosX;
        this.indicator2PosZ = indicator2PosZ;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.playerMP = (EntityPlayerMP) player;
        this.ticks = 10;    
    }

    private static ClaimStateEnum getState(int dim, int chunkX, int chunkZ, EntityPlayer player) {
        ClaimStateEnum state;
        
        ClaimedChunk chunk = LMWorldServer.inst.claimedChunks.getChunk(dim, chunkX, chunkZ);
        LMPlayerServer p = LMWorldServer.inst.getPlayer(player);

        if (chunk == null) state = ClaimStateEnum.NONE;
        else if (!p.equalsPlayer(chunk.getOwnerS())) state = ClaimStateEnum.ENEMY;
        else if (chunk.isChunkloaded) state = ClaimStateEnum.LOADED;
        else state = ClaimStateEnum.OWNER;
        return state;
        
    }

    public void update() {
        ticks++;
        if (ticks >= 10) {
            ticks = 0;
            MinecraftServer s = MinecraftServer.getServer();
            WorldServer server = s.worldServerForDimension(dim);
            state = getState(dim, chunkX, chunkZ, playerMP);

            S23PacketBlockChange main = new S23PacketBlockChange(markerPosX, markerPosY, markerPosZ, server);
            main.field_148883_d = ConfiguredBlocks.markerBlock();
            main.field_148884_e = 0;

            S23PacketBlockChange indicator1 = new S23PacketBlockChange(indicator1PosX, markerPosY, indicator1PosZ, server);
            indicator1.field_148883_d = state.getMarker();
            indicator1.field_148884_e = 0;
            S23PacketBlockChange indicator2 = new S23PacketBlockChange(indicator2PosX, markerPosY, indicator2PosZ, server);
            indicator2.field_148883_d = state.getMarker();
            indicator2.field_148884_e = 0;

            //playerMP.playerNetServerHandler.sendPacket(main);
            //playerMP.playerNetServerHandler.sendPacket(indicator1);
            //playerMP.playerNetServerHandler.sendPacket(indicator2);
            DelayedPacketSender.sendDelayedPacket(main, playerMP, 0.2F, true);
            DelayedPacketSender.sendDelayedPacket(indicator1, playerMP, 0.2F, true);
            DelayedPacketSender.sendDelayedPacket(indicator2, playerMP, 0.2F, true);
        }
    }

    public void destroy() {
        MinecraftServer s = MinecraftServer.getServer();
        WorldServer server = s.worldServerForDimension(dim);

        S23PacketBlockChange main = new S23PacketBlockChange(markerPosX, markerPosY, markerPosZ, server);
        main.field_148883_d = server.getBlock(markerPosX, markerPosY, markerPosZ);
        main.field_148884_e = 0;

        S23PacketBlockChange indicator1 = new S23PacketBlockChange(indicator1PosX, markerPosY, indicator1PosZ, server);
        indicator1.field_148883_d = server.getBlock(indicator1PosX, markerPosY, indicator1PosZ);
        indicator1.field_148884_e = 0;
        S23PacketBlockChange indicator2 = new S23PacketBlockChange(indicator2PosX, markerPosY, indicator2PosZ, server);
        indicator2.field_148883_d = server.getBlock(indicator2PosX, markerPosY, indicator2PosZ);
        indicator2.field_148884_e = 0;

        //playerMP.playerNetServerHandler.sendPacket(main);
        //playerMP.playerNetServerHandler.sendPacket(indicator1);
        //playerMP.playerNetServerHandler.sendPacket(indicator2);
        DelayedPacketSender.sendDelayedPacket(main, playerMP, 1F, false);
        DelayedPacketSender.sendDelayedPacket(indicator1, playerMP, 1F, false);
        DelayedPacketSender.sendDelayedPacket(indicator2, playerMP, 1F, false);

     
}    

}
