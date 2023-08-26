package co.uk.mommyheather.ftbushovel.config;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

//TODO : MAKE THESE ALL CONFIGURABLE
public class ConfiguredBlocks {
    private static Item controlItem;
    private static Block markerBlock;
    private static Block indicatorNone;
    private static Block owner;
    private static Block loaded;
    private static Block enemy;

    public static Item controlItem() {
        if (controlItem == null) {
            controlItem = (Item) Item.itemRegistry.getObject(FTBUShovelConfig.BlocksGroup.controlItem.get());
        }
        return controlItem;
    }
    public static Block markerBlock() {
        if (markerBlock == null) {
            markerBlock = (Block) Block.blockRegistry.getObject(FTBUShovelConfig.BlocksGroup.markerBlock.get());
        }
        return markerBlock;
    }
    public static Block indicatorNone() {
        if (indicatorNone == null) {
            indicatorNone = (Block) Block.blockRegistry.getObject(FTBUShovelConfig.BlocksGroup.unclaimedBlock.get());
        }
        return indicatorNone;
    }
    public static Block owner() {
        if (owner == null) {
            owner = (Block) Block.blockRegistry.getObject(FTBUShovelConfig.BlocksGroup.ownerBlock.get());
        }
        return owner;
    }
    public static Block loaded() {
        if (loaded == null) {
            loaded = (Block) Block.blockRegistry.getObject(FTBUShovelConfig.BlocksGroup.loadedBlock.get());
        }
        return loaded;
    }
    public static Block enemy() {
        if (enemy == null) {
            enemy = (Block) Block.blockRegistry.getObject(FTBUShovelConfig.BlocksGroup.enemyBlock.get());
        }
        return enemy;
    }
}
