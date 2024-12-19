package me.despical.ewduels.arena;

import me.despical.commons.compat.ActionBar;
import me.despical.commons.compat.Titles;
import me.despical.commons.compat.XPotion;
import me.despical.commons.miscellaneous.AttributeUtils;
import me.despical.commons.miscellaneous.MiscUtils;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.scoreboard.ScoreboardManager;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Arena extends BukkitRunnable {

    private static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);
    private static final ChatManager chatManager = plugin.getChatManager();

    private final String id;
    private final ScoreboardManager scoreboardManager;
    private final Set<Bat> vehicles;
    private final Set<Block> placedBlocks;
    private final Set<Runnable> runnables;
    private final Map<Team, TeamData> teams;
    private final Map<GameLocation, Location> locations;

    private int timer;
    private int roundTimer;
    private int tick;
    private boolean skipCurrentTick;
    private boolean ready;
    private boolean started;
    private String mapName;
    private User lastScoredPlayer;
    private SetupMode setupMode;
    private ArenaState arenaState = ArenaState.INACTIVE;

    Arena(String id) {
        this.id = id;
        this.scoreboardManager = new ScoreboardManager(plugin, this);
        this.vehicles = new HashSet<>();
        this.placedBlocks = new HashSet<>();
        this.runnables = new HashSet<>();
        this.teams = new EnumMap<>(Team.class);
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

        scoreboardManager.updateScoreboards();
    }

    private void scheduleTask(Runnable runnable) {
        runnables.add(runnable);
    }

    public boolean isArenaState(ArenaState arenaState, ArenaState... arenaStates) {
        if (this.arenaState == arenaState) return true;

        for (ArenaState state : arenaStates) {
            if (this.arenaState == state) {
                return true;
            }
        }

        return false;
    }

    private Team getRandomAvailableTeam() {
        int random = ThreadLocalRandom.current().nextInt(2);
        Team team = Team.values()[random];

        if (!teams.containsKey(team)) {
            return team;
        }

        TeamData teamData = teams.get(team);

        if (teamData.hasQuit()) {
            return team;
        }

        return team.getOpposite();
    }

    public void addPlayer(User user) {
        Team team = getRandomAvailableTeam();
        user.setTeam(team);

        TeamData teamData = new TeamData(team);
        teamData.setUser(user);

        teams.put(team, teamData);

        if (getPlayers().size() == 2) {
            timer = plugin.<Integer>getOption(Option.START_COUNTDOWN);
        }

        scoreboardManager.createScoreboard(user);
    }

    public void removePlayer(User user) {
        teams.get(user.getTeam()).setQuit(true);

        scoreboardManager.removeScoreboard(user);
    }

    public List<User> getPlayers() {
        return teams.values()
            .stream()
            .filter(TeamData::isUserOnline)
            .filter(Predicate.not(TeamData::hasQuit))
            .map(TeamData::getUser)
            .toList();
    }

    public User getPlayer(Team team) {
        return teams.get(team).getUser();
    }

    public boolean isInArena(User user) {
        return getPlayers().stream().anyMatch(user::equals);
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

    public int setTimer(int timer) {
        this.timer = timer;

        scoreboardManager.updateScoreboards();
        return timer;
    }

    private void decreaseTimer() {
        timer--;

        scoreboardManager.updateScoreboards();
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void addScore(Team team) {
        teams.get(team).addScore();

        scoreboardManager.updateScoreboards();
    }

    public int getScore(Team team) {
        return teams.get(team).getScore();
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void start() {
        if (started) {
            return;
        }

        started = true;
        arenaState = ArenaState.WAITING;

        runTaskTimer(plugin, 20, 5);
    }

    public void stop() {
        if (!started) {
            return;
        }

        this.cancel();
    }

    public void handleQuit(User loser) {
        if (getPlayers().size() - 1 == 1) {
            endTheGameFor(loser);
        }

        teams.get(loser.getTeam()).setQuit(true);

        plugin.getUserManager().removeUser(loser.getUniqueId());
    }

    private void removeUser(User user) {
        Player player = user.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.teleport(this.getLocation(GameLocation.END));

        InventorySerializer.loadInventory(plugin, player);

        scoreboardManager.removeScoreboard(user);

        user.addStat(StatisticType.GAMES_PLAYED, 1);
        user.addStat(StatisticType.KILL, user.getStat(StatisticType.LOCAL_KILL));
        user.addStat(StatisticType.DEATH, user.getStat(StatisticType.LOCAL_DEATH));
    }

    public void handleLeave(User user) {
        if (arenaState == ArenaState.STARTING) {
            user.sendMessage("queue.can-not-leave-now");
            return;
        }

        destroyVehicles();

        if (arenaState == ArenaState.ENDING) {
            if (timer > 0) {
                removePlayer(user);
            }

            return;
        }

        if (arenaState != ArenaState.IN_GAME) {
            return;
        }

        Player player = user.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.teleport(this.getLocation(GameLocation.END));

        InventorySerializer.loadInventory(plugin, player);

        scoreboardManager.removeScoreboard(user);

        int size = this.getPlayers().size() - 1;

        if (size == 1) {
            endTheGameFor(user);
        }

        teams.get(user.getTeam()).setQuit(true);

        plugin.getUserManager().removeUser(user.getUniqueId());
    }

    private void endTheGameFor(User user) {
        setArenaState(ArenaState.ENDING);
        destroyVehicles();

        timer = plugin.<Integer>getOption(Option.ENDING_COUNTDOWN);

        User winner = getOpponent(user);
        winner.sendFormattedMessage("game-messages.left-the-match", chatManager.getTeamColoredName(user));
        winner.addStat(StatisticType.WIN, 1);
        winner.addStat(StatisticType.WIN_STREAK, 1);

        user.addStat(StatisticType.LOSE, 1);
        user.setStat(StatisticType.WIN_STREAK, 0);

        Player winnerPlayer = winner.getPlayer();

        Titles.sendTitle(winnerPlayer, 5, 60, 5, chatManager.getFormattedMessage("titles.win.title", chatManager.getTeamNameBold(winner)), chatManager.getMessage("titles.win.subtitle"));

        for (String message : chatManager.getSummaryMessage(winner, user)) {
            MiscUtils.sendCenteredMessage(winnerPlayer, message);
        }
    }

    public void handlePlacingBlocks(Block block) {
        placedBlocks.add(block);
    }

    public boolean canBreak(Block block) {
        return placedBlocks.contains(block);
    }

    public void handleBreak(Block block) {
        placedBlocks.remove(block);
    }

    public void restoreTheMap() {
        placedBlocks.forEach(state -> state.setType(Material.AIR));
        placedBlocks.clear();
    }

    public void handleNewPoint(User scorer) {
        if (arenaState != ArenaState.IN_GAME) {
            return;
        }

        skipCurrentTick = true;
        lastScoredPlayer = scorer;

        this.addScore(scorer.getTeam());

        restoreTheMap();

        int score = this.getScore(scorer.getTeam());
        String blueScore = chatManager.getColoredScore(this, Team.BLUE);
        String redScore = chatManager.getColoredScore(this, Team.RED);

        Player scorerPlayer = scorer.getPlayer();
        List<String> newRoundMessages = chatManager.getStringList("game-messages.new-round");

        for (User user : getPlayers()) {
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

        boolean win = score == plugin.<Integer>getOption(Option.POINTS_TO_WIN);
        int countdown = plugin.<Integer>getOption(Option.ROUND_STARTING_COUNTDOWN);

        if (!win && countdown != 0) {
            createVehicles();
        }

        String winnerTeamName = chatManager.getTeamNameBold(scorer);

        if (win) {
            List<User> players = this.getPlayers();
            User loser = players.stream().filter(user -> !user.getName().equals(scorer.getName())).findFirst().orElse(null);
            loser.addStat(StatisticType.LOSE, 1);
            loser.setStat(StatisticType.WIN_STREAK, 0);

            scorer.addStat(StatisticType.WIN, 1);
            scorer.addStat(StatisticType.WIN_STREAK, 1);

            List<String> summaryMessages = chatManager.getSummaryMessage(scorer, loser);

            for (User user : players) {
                Player player = user.getPlayer();
                String titlePath = user.equals(scorer) ? "win" : "lose";

                Titles.sendTitle(player, 5, 60, 5, chatManager.getFormattedMessage("titles.%s.title".formatted(titlePath), winnerTeamName), chatManager.getMessage("titles.%s.subtitle".formatted(titlePath)));

                summaryMessages.forEach(message -> MiscUtils.sendCenteredMessage(player, message));
            }

            setArenaState(ArenaState.ENDING);

            timer = plugin.<Integer>getOption(Option.ENDING_COUNTDOWN);
            return;
        }

        timer = countdown;
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
        for (User user : this.getPlayers()) {
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
        this.getPlayers().forEach(user -> user.sendFormattedMessage(path, params));
    }

    public void handleDeath(User victim, User killer) {
        killer.addStat(StatisticType.LOCAL_KILL, 1);

        String killerName = chatManager.getTeamColoredName(killer);
        String victimName = chatManager.getTeamColoredName(victim);

        for (User user : this.getPlayers()) {
            user.sendFormattedMessage("game-messages.killed-by", victimName, killerName);
        }

        victim.addStat(StatisticType.LOCAL_DEATH, 1);

        scoreboardManager.updateScoreboards();

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
        if (getPlayers().size() != 2 || !isInArena(user)) {
            return null;
        }

        return teams.get(user.getTeam().getOpposite()).getUser();
    }

    @Override
    public void run() {
        if (skipCurrentTick) {
            tick = 0;
            skipCurrentTick = false;

            scoreboardManager.updateScoreboards();
        } else if ((tick += 5) != 20) {
            return;
        } else {
            tick = 0;
        }

        int playerSize = getPlayers().size();

        if (playerSize == 0 && arenaState == ArenaState.WAITING) {
            return;
        }

        List<User> players = this.getPlayers();

        switch (arenaState) {
            case WAITING -> {
                if (playerSize < 2) {
                    for (User user : players) {
                        ActionBar.sendActionBar(user.getPlayer(), chatManager.getMessage("queue-messages.action-bar"));
                    }

                    return;
                }

                setArenaState(ArenaState.STARTING);
            }

            case STARTING -> {
                if (timer > 0) {
                    if (playerSize != 2) {
                        setArenaState(ArenaState.WAITING);
                    } else {
                        for (User user : players) {
                            user.sendFormattedMessage("game-messages.starts-in-5-and-less", timer, timer != 1 ? "s" : "");

                            Titles.sendTitle(user.getPlayer(), 5, 22, 5, chatManager.getListElement("titles.starts-in-5-and-less.title", 5 - timer), chatManager.getListElement("titles.starts-in-5-and-less.subtitle", 5 - timer));
                        }

                        scoreboardManager.updateScoreboards();
                    }

                    if (timer-- != 0) {
                        return;
                    }
                }

                List<String> gameExplanation = chatManager.getStringList("game-messages.explanation");

                for (User user : players) {
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

                scoreboardManager.updateScoreboards();

                if (timer >= 0 && lastScoredPlayer != null) {
                    if (timer == 0) {
                        destroyVehicles();
                    } else {
                        for (User user : players) {
                            Player player = user.getPlayer();

                            Titles.sendTitle(player, 0, 25, 2, chatManager.getFormattedMessage("titles.new-score.title", chatManager.getTeamColoredName(lastScoredPlayer)), chatManager.getFormattedMessage("titles.new-score.subtitle", timer));
                        }
                    }

                    decreaseTimer();
                }

                int y = plugin.<Integer>getOption(Option.SEND_TO_THE_START_POS_Y);

                for (User user : players) {
                    Player player = user.getPlayer();

                    if (player.getLocation().getBlockY() < y) {
                        teleportToStart(user);

                        broadcastMessage("game-messages.fell-into-void", chatManager.getTeamColoredName(user));

                        user.addStat(StatisticType.LOCAL_DEATH, 1);
                    }
                }
            }

            case ENDING -> {
                if (timer > 0) {
                    decreaseTimer();
                    return;
                }

                restoreTheMap();

                for (User user : players) {
                    removeUser(user);
                }

                setArenaState(ArenaState.RESTARTING);
            }

            case RESTARTING -> {
                teams.clear();

                vehicles.forEach(Entity::remove);
                vehicles.clear();

                setArenaState(ArenaState.WAITING);
            }
        }
    }
}
