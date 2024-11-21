package me.despical.ewduels;

import me.despical.commandframework.CommandFramework;
import me.despical.ewduels.arena.ArenaManager;
import me.despical.ewduels.arena.ArenaRegistry;
import me.despical.ewduels.command.ArenaCommands;
import me.despical.ewduels.command.PlayerCommands;
import me.despical.ewduels.event.GeneralEvents;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Main extends JavaPlugin {

    private ArenaRegistry arenaRegistry;
    private ArenaManager arenaManager;
    private UserManager userManager;
    private ChatManager chatManager;
    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        arenaRegistry = new ArenaRegistry(this);
        arenaManager = new ArenaManager();
        userManager = new UserManager();
        chatManager = new ChatManager(this);
        commandFramework = new CommandFramework(this);

        new GeneralEvents(this);

        new PlayerCommands();
        new ArenaCommands();
    }

    public ArenaRegistry getArenaRegistry() {
        return arenaRegistry;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public CommandFramework getCommandFramework() {
        return commandFramework;
    }
}
