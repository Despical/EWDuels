package me.despical.ewduels.arena;

import me.despical.commons.compat.ActionBar;
import me.despical.commons.compat.Titles;
import me.despical.commons.compat.XPotion;
import me.despical.commons.miscellaneous.AttributeUtils;
import me.despical.commons.miscellaneous.MiscUtils;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.setup.SetupMode;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import me.despical.ewduels.util.Utils;
import me.despical.fileitems.SpecialItem;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
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
    private static final ChatManager chatManager = plugin.getChatManager();
    private final String id;
    private final Set<Bat> vehicles;
    private final Set<Block> placedBlocks;
    private final Map<Team, User> players;
    private final Map<GameLocation, Location> locations;
    private int timer;
    private int roundTimer;
    private boolean ready;
    private boolean started;
    private User lastScoredPlayer;
    private SetupMode setupMode;
    private ArenaState arenaState = ArenaState.INACTIVE;

    Arena(String id) {
        this.id = id;
        this.vehicles = new HashSet<>();
        this.placedBlocks = new HashSet<>();
        this.players = new EnumMap<>(Team.class);
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

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public boolean isArenaState(ArenaState arenaState) {
        return this.arenaState == arenaState;
    }

    public void addPlayer(User user) {
        players.put(user.getTeam(), user);

        if (players.size() == 2) {
            timer = plugin.<Integer>getOption(Option.START_COUNTDOWN);
        }
    }

    public void removePlayer(User user) {
        players.remove(user.getTeam());
    }

    public List<User> getPlayers() {
        return players.values()
            .stream()
            .filter(user -> user.getPlayer() != null)
            .toList();
    }

    public User getPlayer(Team team) {
        return players.get(team);
    }

    public boolean isInArena(User user) {
        return players.containsValue(user);
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

    public int getRoundTimer() {
        return roundTimer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
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

    public void handleQuit(User loser) {
        players.remove(loser.getTeam());

        if (players.size() == 1) {
            User winner = players.get(0);

            winner.sendRawMessage("You win, the other player has quit the game!");
            winner.addStat(StatisticType.GAMES_PLAYED, 1);
            winner.addStat(StatisticType.WIN, 1);

            loser.addStat(StatisticType.LOSE, 1);
        }

        plugin.getUserManager().removeUser(loser.getUniqueId());
    }

    public void handlePlacingBlocks(Block block) {
        placedBlocks.add(block);
    }

    public boolean canBreak(Block block) {
        return placedBlocks.contains(block);
    }

    public void restoreTheMap() {
        placedBlocks.forEach(state -> state.setType(Material.AIR));
        placedBlocks.clear();
    }

    public void handleNewPoint(User scorer) {
        lastScoredPlayer = scorer;

        List<String> newRoundMessages = chatManager.getStringList("game-messages.new-round");

        Player scorerPlayer = scorer.getPlayer();
        scorer.addStat(StatisticType.LOCAL_SCORE, 1);

        restoreTheMap();

        int score = scorer.getStat(StatisticType.LOCAL_SCORE);
        String blueScore = chatManager.getColoredScore(this, Team.BLUE);
        String redScore = chatManager.getColoredScore(this, Team.RED);

        for (User user : players.values()) {
            teleportToStart(user);
            giveKit(user);

            Player player = user.getPlayer();
            String opponentName = chatManager.getTeamColoredBoldName(scorer);

            for (String message : newRoundMessages) {
                message = chatManager.getFormattedRawMessage(message, opponentName, (int) scorerPlayer.getHealth(),
                    chatManager.getBreak(score), scorer.getTeam() == Team.BLUE ? blueScore : redScore, scorer.getTeam() == Team.BLUE ? redScore : blueScore);

                MiscUtils.sendCenteredMessage(player, message);
            }
        }

        createVehicles();

        int pointsToWin = plugin.<Integer>getOption(Option.POINTS_TO_WIN);
        String winnerTeamName = chatManager.getTeamNameBold(scorer);

        if (score == pointsToWin) {
            User loser = players.values().stream().filter(user -> !user.getName().equals(scorer.getName())).findFirst().orElse(null);
            loser.addStat(StatisticType.LOSE, 1);
            scorer.addStat(StatisticType.WIN, 1);

            List<String> summaryMessages = chatManager.getSummaryMessage(scorer, loser);

            for (User user : players.values()) {
                Player player = user.getPlayer();

                if (user.equals(scorer)) {
                    Titles.sendTitle(player, 5, 50, 5, chatManager.getFormattedMessage("titles.win.title", winnerTeamName), chatManager.getMessage("titles.win.subtitle"));
                } else {
                    Titles.sendTitle(player, 5, 50, 5, chatManager.getFormattedMessage("titles.lose.title", winnerTeamName), chatManager.getMessage("titles.lose.subtitle"));
                }

                summaryMessages.forEach(message -> MiscUtils.sendCenteredMessage(player, message));
            }

            setArenaState(ArenaState.ENDING);

            timer = plugin.<Integer>getOption(Option.ENDING_COUNTDOWN);
            return;
        }

        timer = plugin.<Integer>getOption(Option.ROUND_STARTING_COUNTDOWN);
    }

    private void giveKit(User user) {
        Player player = user.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        AttributeUtils.healPlayer(player);

        Collection<SpecialItem> gameKit = plugin.getItemManager().getItemsFromCategory("ewduels-kit");
        Color color = user.getTeam().getColor();

        for (SpecialItem item : gameKit) {
            ItemStack itemStack = item.getItemStack();

            if (Utils.isArmor(itemStack.getType())) {
                Utils.equipArmorToCorrectSlot(player, itemStack, color);
            } else {
                player.getInventory().setItem(item.<Integer>getCustomKey("slot"), item.getItemStack());
            }
        }
    }

    private void createVehicles() {
        for (User user : players.values()) {
            Location location = this.getLocation(user.getTeam().getStartLocation());
            Bat bat = location.getWorld().spawn(location, Bat.class);
            bat.addPotionEffect(XPotion.INVISIBILITY.buildInvisible(Integer.MAX_VALUE, 1));

            Utils.disableEntityAI(bat);

            vehicles.add(bat);

            bat.setPassenger(user.getPlayer());
        }
    }

    public void destroyVehicles() {
        for (Bat bat : vehicles) {
            bat.eject();
            bat.remove();
        }

        vehicles.clear();
    }

    public void broadcastMessage(String path, Object... params) {
        players.values().forEach(user -> user.sendFormattedMessage(path, params));
    }

    public void handleDeath(User victim, User killer) {
        killer.addStat(StatisticType.KILL, 1);

        String killerName = chatManager.getTeamColoredName(killer);
        String victimName = chatManager.getTeamColoredName(victim);

        for (User user : players.values()) {
            user.sendFormattedMessage("game-messages.killed-by", victimName, killerName);
        }

        victim.addStat(StatisticType.DEATH, 1);

        resetPlayerPosition(victim);
    }

    public void resetPlayerPosition(User user) {
        teleportToStart(user);
        giveKit(user);
    }

    public void teleportToStart(User user) {
        Location location = this.getLocation(user.getTeam().getStartLocation());
        Player player = user.getPlayer();
        player.setFallDistance(0F);
        player.teleport(location);
    }

    public User getOpponent(User user) {
        if (players.size() != 2 || !players.containsValue(user)) {
            return null;
        }

        return players.get(user.getTeam().getOpposite());
    }

    @Override
    public void run() {
        int playerSize = players.size();

        if (playerSize == 0 && arenaState == ArenaState.WAITING) {
            return;
        }

        switch (arenaState) {
            case WAITING -> {
                if (playerSize < 2) {
                    for (User user : players.values()) {
                        ActionBar.sendActionBar(user.getPlayer(), chatManager.getMessage("queue-messages.action-bar"));
                    }

                    return;
                }

                if (timer > 0) {
                    for (User user : players.values()) {
                        user.sendFormattedMessage("game-messages.starts-in-5-and-less", timer, timer != 1 ? "s" : "");

                        Titles.sendTitle(user.getPlayer(), 5, 22, 5, chatManager.getListElement("titles.starts-in-5-and-less.title", 5 - timer), chatManager.getListElement("titles.starts-in-5-and-less.subtitle", 5 - timer));
                    }

                    if (--timer == 0) {
                        setArenaState(ArenaState.STARTING);
                    }
                }
            }

            case STARTING -> {
                List<String> gameExplanation = chatManager.getStringList("game-messages.explanation");

                for (User user : players.values()) {
                    Player player = user.getPlayer();

                    if (player == null) {
                        return;
                    }

                    Titles.sendTitle(player, 5, 10, 5, chatManager.getMessage("titles.started.title"), chatManager.getMessage("titles.started.subtitle"));

                    player.teleport(this.getLocation(user.getTeam().getStartLocation()));
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setFoodLevel(20);
                    player.setGameMode(GameMode.SURVIVAL);

                    giveKit(user);

                    User opponent = getOpponent(user);

                    for (String message : gameExplanation) {
                        message = chatManager.getFormattedRawMessage(message, opponent.getName());

                        MiscUtils.sendCenteredMessage(player, message);
                    }
                }

                setArenaState(ArenaState.IN_GAME);
            }

            case IN_GAME -> {
                roundTimer++;

                if (timer >= 0 && lastScoredPlayer != null) {
                    if (timer == 0) {
                        destroyVehicles();
                    } else {
                        for (User user : players.values()) {
                            Player player = user.getPlayer();

                            Titles.sendTitle(player, 0, 25, 2, chatManager.getFormattedMessage("titles.new-score.title", chatManager.getTeamColoredName(lastScoredPlayer)), chatManager.getFormattedMessage("titles.new-score.subtitle", timer));
                        }

                    }

                    timer--;
                }

                int y = plugin.<Integer>getOption(Option.SEND_TO_THE_START_POS_Y);

                for (User user : players.values()) {
                    Player player = user.getPlayer();

                    if (player.getLocation().getBlockY() < y) {
                        teleportToStart(user);

                        broadcastMessage("game-messages.fell-into-void", chatManager.getTeamColoredName(user));

                        user.addStat(StatisticType.DEATH, 1);
                    }
                }
            }

            case ENDING -> {
                if (timer-- > 0) {
                    return;
                }

                for (User user : players.values()) {
                    Player player = user.getPlayer();
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.teleport(this.getLocation(GameLocation.END));

                    InventorySerializer.loadInventory(plugin, player);

                    user.addStat(StatisticType.GAMES_PLAYED, 1);
                }

                setArenaState(ArenaState.RESTARTING);
                restoreTheMap();
            }

            case RESTARTING -> {
                players.clear();
            }
        }
    }
}
