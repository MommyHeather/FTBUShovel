package co.uk.mommyheather.ftbushovel.net;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S23PacketBlockChange;


public class DelayedPacketSender extends Thread{


    private static ArrayList<DelayedPacketSender> runners = new ArrayList<>();

    public static void sendDelayedPacket(S23PacketBlockChange packet, EntityPlayerMP player, float maxS, boolean cancellable) {
        int delay = ThreadLocalRandom.current().nextInt(0, (int) (maxS * 1000));
        DelayedPacketSender runner = new DelayedPacketSender(packet, player, delay);
        if (cancellable) {
            runners.add(runner);
        }
        runner.start();
    }

    public static void cancel(EntityPlayerMP player) {
        ArrayList<DelayedPacketSender> runnersTemp = new ArrayList<>(runners);
        runners.clear();
        for (DelayedPacketSender runner : runnersTemp) {
            if (runner.player.getUniqueID() == player.getUniqueID()) {
                runner.interrupt();
            } 
            else runners.add(runner);
        }
    }

    private S23PacketBlockChange packet;
    private EntityPlayerMP player;
    private int delay;

    public DelayedPacketSender(S23PacketBlockChange packet, EntityPlayerMP player, int delay) {
        this.packet = packet;
        this.player = player;
        this.delay = delay;
    }

    public void run() {
        try {
            sleep(delay);
            player.playerNetServerHandler.sendPacket(packet);
        } catch (InterruptedException e) {
        }
    }
}    
