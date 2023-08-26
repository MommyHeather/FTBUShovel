package co.uk.mommyheather.ftbushovel.config;

import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.config.ConfigEntryString;
import ftb.lib.api.config.ConfigFile;
import ftb.lib.api.config.ConfigRegistry;
import latmod.lib.annotations.Info;

public class FTBUShovelConfig {
    public static final ConfigFile configFile = new ConfigFile("ftbushovel");

    public static void load()
	{
		configFile.setFile(new File(FTBLib.folderLocal, "ftbushovel/config.json"));
		configFile.setDisplayName("FTBUShovel");
		configFile.addGroup("Blocks", BlocksGroup.class);
		configFile.addGroup("Commands", CommandsGroup.class);
		
		ConfigRegistry.add(configFile);
		configFile.load();
	}


    public static class BlocksGroup {
        @Info("The item used to clam, unclaim, load and unload chunks.")
        public static final ConfigEntryString controlItem = new ConfigEntryString("controlItem", "minecraft:golden_shovel");

        @Info("The block used to mark chunk corners.")
        public static final ConfigEntryString markerBlock = new ConfigEntryString("markerBlock", "minecraft:glowstone");
		
        @Info("The block used to mark a chunk as unclaimed.")
        public static final ConfigEntryString unclaimedBlock = new ConfigEntryString("unclaimedBlock", "minecraft:wool");
		
        @Info("The block used to mark a chunk as claimed by the user.")
        public static final ConfigEntryString ownerBlock = new ConfigEntryString("ownerBlock", "minecraft:gold_block");
		
        @Info("The block used to mark a chunk as loaded.")
        public static final ConfigEntryString loadedBlock = new ConfigEntryString("loadedBlock", "minecraft:lapis_block");
		
        @Info("The block used to mark a chunk as claimed by an enemy.")
        public static final ConfigEntryString enemyBlock = new ConfigEntryString("enemyBlock", "minecraft:netherrack");
    }

    public static class CommandsGroup {
        @Info("Whether to enable the /claim command, which gives a user the item configured for claiming.")
        public static final ConfigEntryBool claimCommand = new ConfigEntryBool("claimCommand", true);

        @Info("Whether to enable the /tpa commands, which let users teleport to one another.")
        public static final ConfigEntryBool tpaCommand = new ConfigEntryBool("tpaCommand", true);

    }
}
