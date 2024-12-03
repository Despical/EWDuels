package me.despical.ewduels.arena;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.arena.setup.SetupMode;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Arena extends BukkitRunnable {

    private static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);
    private final String id;
    private final Map<GameLocation, Location> locations;
    private boolean ready;
    private SetupMode setupMode;

    private final List<User> players;
    private final Map<User, Integer> scores;

    Arena(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
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

    public void addPlayer(User user) {
        players.add(user);
    }

    public void removePlayer(User user) {
        players.remove(user);
    }

    public User getRedTeam() {
        return this.players.get(0);
    }

    public User getBlueTeam() {
        return this.players.get(1);
    }

    public boolean isRedTeam(User player) {
        return getRedTeam().equals(player);
    }

    public boolean isBlueTeam(User player) {
        return getBlueTeam().equals(player);
    }

    public Team getTeamOf(User player) {
        return isRedTeam(player) ? Team.RED : Team.BLUE;
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
        User first = this.players.get(0);
        User second = this.players.get(1);

        if (first == null || second == null) {
            return;
        }

        Player firstPlayer = first.getPlayer();
        Player secondPlayer = second.getPlayer();

        firstPlayer.teleport(this.getLocation(GameLocation.FIRST_PLAYER));
        secondPlayer.teleport(this.getLocation(GameLocation.SECOND_PLAYER));

        this.scores.put(first, 0);
        this.scores.put(second, 0);

        this.runTaskTimer(plugin, 20,20);
    }

    public int getScore(User player) {
        return this.scores.get(player);
    }

    public void addScore(User player) {
        this.scores.put(player, getScore(player) + 1);
    }

    public void stop() {
        this.players.clear();
        this.scores.clear();

        this.cancel();
    }

    @Override
    public void run() {
        User first = this.players.get(0);
        User second = this.players.get(1);

        int firstScore = this.scores.get(first);
        int secondScore = this.scores.get(second);

        if (firstScore == 5) {
            plugin.getArenaManager().endGame(this, first, second);
            return;
        }

        if (secondScore == 5) {
            plugin.getArenaManager().endGame(this, second, first);
            return;
        }
    }

    public enum Team {

        RED, BLUE

    }

}
