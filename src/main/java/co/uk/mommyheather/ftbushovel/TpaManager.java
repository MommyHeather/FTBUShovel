package co.uk.mommyheather.ftbushovel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import ftb.lib.LMDimUtils;
import ftb.utils.mod.FTBU;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class TpaManager {
    private static HashMap<String, TpaPlayer> playerRequests = new HashMap<>(); //String playername, player - each one has a list of requests that are pending

    private static ArrayList<TpaTask> tasks = new ArrayList<>();

    private static class TpaTask {
        private Long expiry;
        private Runnable execute;
        private String sender;
        private String receiver;

        public boolean checkAndExecute() {
            if (System.currentTimeMillis() < expiry) return false;

            execute.run();

            return true;
        }

        public TpaTask(Long expiry, Runnable execute, String sender, String receiver) {
            this.expiry = expiry;
            this.execute = execute;
            this.sender = sender;
            this.receiver = receiver;
        }

        public boolean checkCancel(String sender, String receiver) {
            return this.sender.equals(sender) && this.receiver.equals(receiver);
        }
    }

    private static class TpaRequest {
        private String playerName;
        private EntityPlayer player;
        public TpaRequest(String playerName, EntityPlayer player) {
            this.playerName = playerName;
            this.player = player;
        }

    }


    private static class TpaPlayer {
        private EntityPlayer self;
        private HashMap<String, TpaRequest> requests; //String playername, request instance - only one request to each player per player

        public TpaPlayer(EntityPlayer self) {
            this.self = self;
            requests = new HashMap<>();
        }

        public boolean checkExistingRequest(String sender) {
            return requests.containsKey(sender);
        }

        public void makeRequest(EntityPlayer sender) {
            requests.put(sender.getDisplayName(), new TpaRequest(sender.getDisplayName(), sender));
            ChatComponentText text = new ChatComponentText(sender.getDisplayName() + " is requesting TPA! Click here to accept.");
            text.getChatStyle().setColor(EnumChatFormatting.YELLOW);
            text.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + sender.getDisplayName()));
            text.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("/tpaaccept " + sender.getDisplayName())));
            self.addChatMessage(text);

            addTask(System.currentTimeMillis() + 30000L, () -> {
                requests.remove(sender.getDisplayName());
                ChatComponentText text1 = new ChatComponentText("TPA request to " + self.getDisplayName() + " has expired!");
                text1.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatComponentMessage(text1);

                
                ChatComponentText text2 = new ChatComponentText("TPA request from " + sender.getDisplayName() + " has expired!");
                text2.getChatStyle().setColor(EnumChatFormatting.RED);
                self.addChatComponentMessage(text2);
            }, sender.getDisplayName(), self.getDisplayName()); //30 seconds until request expires
        }

        public void acceptRequest(EntityPlayerMP sender) {
            if (requests.containsKey(sender.getDisplayName())) {
                TpaRequest request = requests.remove(sender.getDisplayName());

                //System.out.println(self.dimension);

                LMDimUtils.teleportPlayer(sender, self.posX, self.posY, self.posZ, self.dimension);

                //sender.mcServer.getConfigurationManager().transferPlayerToDimension(sender, self.dimension);
                //sender.playerNetServerHandler.setPlayerLocation(self.posX, self.posY, self.posZ, self.rotationYaw, self.rotationPitch);
                TpaManager.removeTask(sender.getDisplayName(), self.getDisplayName());

                ChatComponentText text = new ChatComponentText("TPA request accepted!");
                text.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatComponentMessage(text);
                self.addChatComponentMessage(text);
            }
        }

    }


    public static boolean checkExistingRequest(String sender, String receiver) {
        if (playerRequests.containsKey(receiver)) {
            TpaPlayer player = playerRequests.get(receiver);
            return player.checkExistingRequest(sender);

        }
        return false;
    }

    public static void makeRequest(EntityPlayer sender, EntityPlayer receiver) {
        if (!playerRequests.containsKey(receiver.getDisplayName())) {
            playerRequests.put(receiver.getDisplayName(), new TpaPlayer(receiver));
        }

        TpaPlayer receiver2 = playerRequests.get(receiver.getDisplayName());
        receiver2.makeRequest(sender);
    }

    
    public static void acceptRequest(EntityPlayerMP sender, EntityPlayerMP receiver) {
        if (playerRequests.containsKey(receiver.getDisplayName())) {
            playerRequests.get(receiver.getDisplayName()).acceptRequest(sender);
        }
    }


    public static void checkTasks() {
        ArrayList<TpaTask> temp = new ArrayList<>(tasks);
        tasks.clear();

        for (TpaTask tpaTask : temp) {
            if (!tpaTask.checkAndExecute()) {
                tasks.add(tpaTask);
            }
        }
    }

    public static void addTask(Long expiry, Runnable run, String sender, String receiver) {
        TpaTask task = new TpaTask(expiry, run, sender, receiver);
        tasks.add(task);
    }

    public static void removeTask(String sender, String receiver) {
        ArrayList<TpaTask> temp = new ArrayList<>(tasks);
        tasks.clear();

        for (TpaTask tpaTask : temp) {
            if (!tpaTask.checkCancel(sender, receiver)) {
                tasks.add(tpaTask);
            }
        }

    }


}
