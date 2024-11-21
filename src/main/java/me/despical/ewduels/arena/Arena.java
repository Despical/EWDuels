package me.despical.ewduels.arena;

import me.despical.ewduels.Main;
import me.despical.ewduels.arena.setup.SetupMode;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.Location;

import java.util.*;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Arena {

    private static final Main plugin = Main.getPlugin(Main.class);

    private SetupMode setupMode;

    private final String id;
    private final Set<User> players;
    private final Map<GameLocation, Location> locations;

    Arena(String id) {
        this.id = id;
        this.players = new HashSet<>();
        this.locations = new EnumMap<>(GameLocation.class);
    }

    public String getId() {
        return id;
    }

    public void addPlayer(User user) {
        players.add(user);
    }

    public void removePlayer(User user) {
        players.remove(user);
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

    public void setSetupMode(SetupMode setupMode) {
        this.setupMode = setupMode;
    }

    public boolean isSetupMode() {
        return setupMode != null;
    }
}
