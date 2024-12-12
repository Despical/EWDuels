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
import me.despical.ewduels.option.ConfigOptions;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.UserManager;
import me.despical.fileitems.ItemManager;
import me.despical.fileitems.ItemOption;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class EWDuels extends JavaPlugin {

    private ConfigOptions configOptions;
    private ArenaRegistry arenaRegistry;
    private ArenaManager arenaManager;
    private UserManager userManager;
    private ChatManager chatManager;
    private ItemManager itemManager;
    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        configOptions = new ConfigOptions(this);
        arenaRegistry = new ArenaRegistry(this);
        chatManager = new ChatManager(this);
        arenaManager = new ArenaManager(this);
        userManager = new UserManager(this);
        itemManager = new ItemManager(this, manager -> ItemOption.enableOptions(ItemOption.AMOUNT));
        itemManager.editItemBuilder(itemBuilder -> itemBuilder.unbreakable(true).flag(ItemFlag.values()));
        itemManager.addCustomKey("slot");
        itemManager.registerItemsFromResources("setup-items.yml", "items");
        itemManager.registerItems("items", "queue-items");
        itemManager.registerItems("ewduels-kit", "kit", ConfigUtils.getConfig(this, "items"));
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

    public <T> T getOption(Option option) {
        return configOptions.getOption(option);
    }

    public boolean isEnabled(Option option) {
        return this.<Boolean>getOption(option);
    }
}
