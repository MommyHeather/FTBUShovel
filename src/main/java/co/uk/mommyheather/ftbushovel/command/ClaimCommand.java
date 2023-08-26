package co.uk.mommyheather.ftbushovel.command;

import co.uk.mommyheather.ftbushovel.config.ConfiguredBlocks;
import co.uk.mommyheather.ftbushovel.config.FTBUShovelConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class ClaimCommand extends CommandBase
{
    public ClaimCommand()
    {
        /*addSubcommand(new Check());
        addSubcommand(new Start());
        addSubcommand(new Force());
        addSubcommand(new Reload());*/
    }

    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }
    
    @Override
    public String getCommandName()
    {
        return "claim";
    }


    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/claim";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!FTBUShovelConfig.CommandsGroup.claimCommand.get()) {
            throw new CommandException("Disabled in config!");
        }
        if (sender instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            EntityItem item = player.dropPlayerItemWithRandomChoice(new ItemStack(ConfiguredBlocks.controlItem()), false);
            item.delayBeforeCanPickup = 0;
            item.func_145797_a(player.getCommandSenderName());
            player.addChatMessage(new ChatComponentText("Succesfully gave you the claim item."));
        }
        else
        {
            throw new CommandException("This must be ran by a player!");
        }
    }

}
