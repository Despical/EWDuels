package me.despical.ewduels;

import me.despical.commandframework.CommandFramework;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.ewduels.arena.ArenaManager;
import me.despical.ewduels.arena.ArenaRegistry;
import me.despical.ewduels.command.ArenaCommands;
import me.despical.ewduels.command.PlayerCommands;
import me.despical.ewduels.event.GeneralEvents;
import me.despical.ewduels.event.InGameEvents;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.user.UserManager;
import me.despical.fileitems.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class EWDuels extends JavaPlugin {

    private ArenaRegistry arenaRegistry;
    private ArenaManager arenaManager;
    private UserManager userManager;
    private ChatManager chatManager;
    private ItemManager itemManager;
    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        arenaRegistry = new ArenaRegistry(this);
        chatManager = new ChatManager(this);
        arenaManager = new ArenaManager(this);
        userManager = new UserManager(this);
        itemManager = new ItemManager(this);
        itemManager.editItemBuilder(itemBuilder -> itemBuilder.unbreakable(true).hideTooltip(true));
        itemManager.addCustomKey("slot");
        itemManager.registerItemsFromResources("setup-items.yml", "items");
        itemManager.registerItems("ewduels-kit", "kit", ConfigUtils.getConfig(this, "ingame-items"));
        commandFramework = new CommandFramework(this);

        new GeneralEvents();
        new InGameEvents();

        new PlayerCommands();
        new ArenaCommands();
    }

    @Override
    public void onDisable() {
        arenaRegistry.saveData();
        userManager.getDatabase().shutdown();
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

    public ItemManager getItemManager() {
        return itemManager;
    }

    public CommandFramework getCommandFramework() {
        return commandFramework;
    }
}
