package me.despical.ewduels.command;

import me.despical.commandframework.Message;
import me.despical.ewduels.Main;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public abstract class AbstractCommandHandler {

    protected static final Main plugin;

    static {
        plugin = Main.getPlugin(Main.class);
        plugin.getCommandFramework().addCustomParameter("User", args -> plugin.getUserManager().getUser(args.getSender()));

        Message.SHORT_ARG_SIZE.setMessage((command, args) -> {
            String correctUsage = plugin.getChatManager().getFormattedMessage("admin-commands.usage", command.usage());

            args.sendMessage(correctUsage);
            return true;
        });
    }

    public AbstractCommandHandler() {
        plugin.getCommandFramework().registerCommands(this);
    }
}
