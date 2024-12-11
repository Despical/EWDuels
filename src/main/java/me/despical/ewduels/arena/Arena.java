package me.despical.ewduels.arena;

import me.despical.commons.compat.ActionBar;
import me.despical.commons.miscellaneous.AttributeUtils;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.setup.SetupMode;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import me.despical.ewduels.util.Utils;
import me.despical.fileitems.SpecialItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
    private boolean started;

    private SetupMode setupMode;
    private ArenaState arenaState = ArenaState.INACTIVE;

    private final String id;
    private final List<User> players;
    private final Set<BlockState> blockStates;
    private final Map<GameLocation, Location> locations;

    Arena(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.blockStates = new HashSet<>();
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
        return players.stream()
            .filter(user -> user.getPlayer() != null)
            .toList();
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
        if (started) {
            return;
        }

        started = true;
        arenaState = ArenaState.WAITING;

        runTaskTimer(plugin, 20, 20);
    }

    public void stop() {
        if (!started) {
            return;
        }

        this.cancel();
    }

    public void handlePlacingBlocks(Block block) {
        blockStates.add(block.getState());
    }

    public boolean canBreak(Block block) {
        return blockStates.contains(block.getState());
    }

    public void restoreTheMap() {
        blockStates.forEach(blockState -> blockState.update(true));
        blockStates.clear();
    }

    public void handleNewPoint(User scorer) {
        scorer.addStat(StatisticType.LOCAL_SCORE, 1);

        restoreTheMap();

        for (int i = 0; i < 2; i++) {
            GameLocation location = i == 0 ? GameLocation.FIRST_PLAYER : GameLocation.SECOND_PLAYER;

            User user = players.get(i);
            user.sendRawMessage("player : " + scorer.getName() + " scored!");

            Player player = user.getPlayer();
            player.teleport(this.getLocation(location));

            AttributeUtils.healPlayer(player);

            giveKit(player, i == 0 ? Color.RED : Color.BLUE);
        }
    }

    private void giveKit(Player player, Color color) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        Collection<SpecialItem> gameKit = plugin.getItemManager().getItemsFromCategory("ewduels-kit");

        for (SpecialItem item : gameKit) {
            ItemStack itemStack = item.getItemStack();

            if (Utils.isArmor(itemStack.getType())) {
                Utils.equipArmorToCorrectSlot(player, itemStack, color);
            } else {
                player.getInventory().setItem(item.<Integer>getCustomKey("slot"), item.getItemStack());
            }
        }
    }

    @Override
    public void run() {
        switch (arenaState) {
            case WAITING -> {
                if (players.size() < 2) {
                    for (User user : players) {
                        ActionBar.sendActionBar(user.getPlayer(), "Waiting for the opponent...");
                    }

                    return;
                }

                setArenaState(ArenaState.STARTING);
            }

            case STARTING -> {
                for (int i = 0; i < 2; i++) {
                    User user = players.get(i);

                    Player player = user.getPlayer();

                    if (player == null) {
                        return;
                    }

                    GameLocation location = i == 0 ? GameLocation.FIRST_PLAYER : GameLocation.SECOND_PLAYER;
                    player.teleport(this.getLocation(location));
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setFoodLevel(20);

                    AttributeUtils.healPlayer(player);

                    giveKit(player, i == 0 ? Color.RED : Color.BLUE);
                }

                setArenaState(ArenaState.IN_GAME);
            }

            case IN_GAME -> {
                for (User user : players) {
                    int score = user.getStat(StatisticType.LOCAL_SCORE);

                    if (score == 5) {
                        user.sendRawMessage("you win!");

                        setArenaState(ArenaState.ENDING);
                    }
                }
            }

            case ENDING -> {
                // Wait a few seconds maybe
                User winner = players.stream().filter(user -> user.getStat(StatisticType.LOCAL_SCORE) == 5).findFirst().orElse(null);
                User loser  = players.stream().filter(user -> !user.getName().equals(winner.getName())).findFirst().orElse(null);

                for (User user : players) {
                    Player player = user.getPlayer();
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.teleport(this.getLocation(GameLocation.LOBBY));

                    InventorySerializer.loadInventory(plugin, player);

                    user.addStat(StatisticType.GAMES_PLAYED, 1);
                    user.sendFormattedMessage("game-messages.ended", winner.getName(), loser.getName());
                }

                winner.addStat(StatisticType.WIN, 1);
                loser.addStat(StatisticType.LOSE, 1);

                setArenaState(ArenaState.RESTARTING);
                restoreTheMap();
            }

            case RESTARTING -> {
                players.clear();
            }
        }
    }
}
