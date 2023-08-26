package co.uk.mommyheather.ftbushovel.command;

import java.util.List;

import co.uk.mommyheather.ftbushovel.TpaManager;
import co.uk.mommyheather.ftbushovel.config.ConfiguredBlocks;
import co.uk.mommyheather.ftbushovel.config.FTBUShovelConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class TpaCommand extends CommandBase
{
    public TpaCommand()
    {
        /*addSubcommand(new Check());
        addSubcommand(new Start());
        addSubcommand(new Force());
        addSubcommand(new Reload());*/
    }

    @Override
    public String getCommandName()
    {
        return "tpa";
    }

    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }


    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/tpa <playername>";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!FTBUShovelConfig.CommandsGroup.tpaCommand.get()) {
            throw new CommandException("Disabled in config!");
        }
        if (sender instanceof EntityPlayerMP)
        {
            EntityPlayerMP self = (EntityPlayerMP) sender;
            if (args.length == 0) {
                throw new CommandException("Type the name of the player you want to teleport to!");
            }
            if (args.length < 1) {
                throw new CommandException("You can only request to one player at a time!");
            }

            EntityPlayerMP other;
            other = getPlayer(sender, args[0]);
            if (other == null) {
                throw new CommandException("Player " + args[0] + " not found!");
            }
            
            if (other.getDisplayName() == self.getDisplayName()) {
                throw new CommandException("You can't TPA to yourself!");
            }

            if (TpaManager.checkExistingRequest(self.getDisplayName(), other.getDisplayName())) {
                throw new CommandException("You already have a pending request to this player!");
            }

            TpaManager.makeRequest(self, other);
            sender.addChatMessage(new ChatComponentText("TPA request sent!"));
        }
        else
        {
            throw new CommandException("This must be ran by a player!");
        }
    }

    
        /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 && sender instanceof EntityPlayerMP ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }

}
