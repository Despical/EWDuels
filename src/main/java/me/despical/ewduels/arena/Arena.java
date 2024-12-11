package me.despical.ewduels.arena;

import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.arena.setup.SetupMode;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import me.despical.ewduels.util.Utils;
import me.despical.fileitems.SpecialItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Arena extends BukkitRunnable {

    private static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);

    private boolean ready;

    private SetupMode setupMode;
    private ArenaState arenaState = ArenaState.INACTIVE;

    private final String id;
    private final List<User> players;
    private final Map<GameLocation, Location> locations;

    Arena(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.locations = new EnumMap<>(GameLocation.class);
    }

    public String getId() {
        return id;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public boolean isArenaState(ArenaState arenaState) {
        return this.arenaState == arenaState;
    }

    public void addPlayer(User user) {
        players.add(user);
    }

    public void removePlayer(User user) {
        players.remove(user);
    }

    public List<User> getPlayers() {
        return players;
    }

    public boolean isInArena(User user) {
        return players.contains(user);
    }

    public void setLocation(GameLocation gameLocation, Location location) {
        locations.put(gameLocation, location);
    }

    public Location getLocation(GameLocation gameLocation) {
        return locations.get(gameLocation);
    }

    public void createSetupSeason(User user) {
        if (setupMode != null) {
            throw new IllegalStateException("Someone has already been set upping the arena!");
        }

        this.setupMode = new SetupMode(plugin, this, user);
    }

    public void endSetupSeason(boolean delete) {
        setupMode.exitSetup(delete);
        setupMode = null;
    }

    public boolean isSetupMode() {
        return setupMode != null;
    }

    public void start() {
        if (players.size() < 2) {
            return;
        }

        for (int i = 0; i < 2; i++) {
            User user = players.get(i);

            Player player = user.getPlayer();

            if (player == null) {
                break;
            }

            InventorySerializer.saveInventoryToFile(plugin, player);

            GameLocation location = i == 0 ? GameLocation.FIRST_PLAYER : GameLocation.SECOND_PLAYER;
            player.teleport(this.getLocation(location));
            player.getInventory().clear();

            Collection<SpecialItem> gameKit = plugin.getItemManager().getItemsFromCategory("ewduels-kit");
            Color color = i == 0 ? Color.RED : Color.BLUE;

            for (SpecialItem item : gameKit) {
                ItemStack itemStack = item.getItemStack();

                if (Utils.isArmor(itemStack.getType())) {
                    Utils.equipArmorToCorrectSlot(player, itemStack, color);
                } else {
                    player.getInventory().setItem(item.<Integer>getCustomKey("slot"), item.getItemStack());
                }
            }

        }
        this.runTaskTimer(plugin, 20, 20);
    }

    public void stop() {
        this.players.clear();

        this.cancel();
    }

    @Override
    public void run() {
        User first = this.players.get(0);
        User second = this.players.get(1);

        int firstScore = 0;
        int secondScore = 0;

        if (firstScore == 5) {

            return;
        }

        if (secondScore == 5) {
            return;
        }
    }
}
