package co.uk.mommyheather.ftbushovel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.uk.mommyheather.ftbushovel.command.ClaimCommand;
import co.uk.mommyheather.ftbushovel.command.TpaAcceptCommand;
import co.uk.mommyheather.ftbushovel.command.TpaCommand;
import co.uk.mommyheather.ftbushovel.config.ConfiguredBlocks;
import co.uk.mommyheather.ftbushovel.config.FTBUShovelConfig;
import co.uk.mommyheather.ftbushovel.tracking.PlayerShovelTracker;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import ftb.lib.LMMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;


@Mod(modid = FTBUShovel.MODID, name = FTBUShovel.NAME, version = FTBUShovel.VERSION, acceptableRemoteVersions = "*")
public class FTBUShovel
{
    public static final String MODID = "ftbushovel";
    public static final String NAME = "FTBUShovel";
    public static final String VERSION = "1.0";
    public static MinecraftServer server;

    public static Logger logger = LogManager.getLogger("FTBUShovel");


	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
        LMMod.create(MODID);
		FTBUShovelConfig.load();
	}

    public FTBUShovel()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ClaimCommand());
        event.registerServerCommand(new TpaCommand());
        event.registerServerCommand(new TpaAcceptCommand());
    }

    @SubscribeEvent
    public void onWorldPostTick(WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        TpaManager.checkTasks();
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        Entity entity = event.entity;
        if (entity.worldObj.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack stack = player.getHeldItem();
        if (stack == null) { 
            PlayerShovelTracker.stopTracking(player);
            return;
        }
        Item item = stack.getItem();
        if (item == null) {
            PlayerShovelTracker.stopTracking(player);
            return;
        }
        if (item.equals(ConfiguredBlocks.controlItem())) {
            PlayerShovelTracker.startTracking(player);
        }
        else {
            PlayerShovelTracker.stopTracking(player);
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.worldObj.isRemote) return;
        PlayerShovelTracker.trackMovement(event.player);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.world.isRemote) return;
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.getHeldItem();
        if (stack == null) return;
        Item item = stack.getItem();
        if (item == null) return;
        if (item.equals(ConfiguredBlocks.controlItem())) {
            ClaimManager.handleInteract(event, player);
        }
    }


}
