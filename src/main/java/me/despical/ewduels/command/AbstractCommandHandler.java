package me.despical.ewduels.command;

import me.despical.commandframework.Message;
import me.despical.ewduels.EWDuels;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public abstract class AbstractCommandHandler {

    protected static final EWDuels plugin;

    static {
        plugin = EWDuels.getPlugin(EWDuels.class);
        plugin.getCommandFramework().addCustomParameter("User", args -> plugin.getUserManager().getUser(args.getSender()));

        Message.SHORT_ARG_SIZE.setMessage((command, args) -> {
            String correctUsage = plugin.getChatManager().getFormattedMessage("commands.correct-usage", command.usage());

            args.sendMessage(correctUsage);
            return true;
        });
    }

    public AbstractCommandHandler() {
        plugin.getCommandFramework().registerCommands(this);
    }
}
