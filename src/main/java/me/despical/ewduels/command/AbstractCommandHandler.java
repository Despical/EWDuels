package me.despical.ewduels.command;

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
    }

    public AbstractCommandHandler() {
        plugin.getCommandFramework().registerCommands(this);
    }
}
