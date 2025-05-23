package me.despical.ewduels;

import me.despical.commandframework.CommandFramework;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.scoreboard.ScoreboardLib;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaManager;
import me.despical.ewduels.arena.ArenaRegistry;
import me.despical.ewduels.command.ArenaCommands;
import me.despical.ewduels.command.PlayerCommands;
import me.despical.ewduels.event.GeneralEvents;
import me.despical.ewduels.event.InGameEvents;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.handler.papi.PlaceholderManager;
import me.despical.ewduels.option.ConfigOptions;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.User;
import me.despical.ewduels.user.UserManager;
import me.despical.ewduels.util.GameLocation;
import me.despical.fileitems.ItemManager;
import me.despical.fileitems.ItemOption;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class EWDuels extends JavaPlugin {

    private ConfigOptions configOptions;
    private ChatManager chatManager;
    private ArenaRegistry arenaRegistry;
    private ArenaManager arenaManager;
    private UserManager userManager;
    private ItemManager itemManager;
    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        ScoreboardLib.setPluginInstance(this);

        configOptions = new ConfigOptions(this);
        chatManager = new ChatManager(this);
        arenaRegistry = new ArenaRegistry(this);
        arenaManager = new ArenaManager(this);
        userManager = new UserManager(this);
        itemManager = new ItemManager(this, manager -> ItemOption.enableOptions(ItemOption.AMOUNT));
        itemManager.editItemBuilder(itemBuilder -> itemBuilder.unbreakable(true).flag(ItemFlag.values()));
        itemManager.addCustomKey("slot");
        itemManager.registerItemsFromResources("setup-items.yml", "items");
        itemManager.registerItems("items", "queue-items");
        itemManager.registerItems("ewduels-kit", "kit", ConfigUtils.getConfig(this, "items"));
        commandFramework = new CommandFramework(this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderManager(this);
        }

        new GeneralEvents();
        new InGameEvents();

        new PlayerCommands();
        new ArenaCommands();
    }

    @Override
    public void onDisable() {
        arenaRegistry.saveData();
        userManager.getDatabase().shutdown();

        for (Arena arena : arenaRegistry.getArenas()) {
            arena.destroyVehicles();

            for (User user : arena.getPlayers()) {
                Player player = user.getPlayer();
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.teleport(arena.getLocation(GameLocation.END));

                user.sendMessage("game-messages.match-cancelled-due-to-reload");

                arena.getScoreboardManager().removeScoreboard(user);

                InventorySerializer.loadInventory(this, player);
            }
        }
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
