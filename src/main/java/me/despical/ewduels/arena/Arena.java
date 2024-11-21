package me.despical.ewduels.arena;

import me.despical.ewduels.Main;
import me.despical.ewduels.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class Arena {

    private static final Main plugin = Main.getPlugin(Main.class);

    private final String id;
    private final Set<User> players;

    Arena(String id) {
        this.id = id;
        this.players = new HashSet<>();
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
}
