package me.despical.ewduels.user;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.user.data.AbstractDatabase;
import me.despical.ewduels.user.data.FlatFileStatistics;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class UserManager {

    private final AbstractDatabase database;
    private final Map<UUID, User> users;

    public UserManager(EWDuels plugin) {
        this.database = new FlatFileStatistics(plugin);
        this.users = new HashMap<>();
    }

    public User addUser(Player player) {
        User user = new User(player);

        users.put(player.getUniqueId(), user);
        return user;
    }

    public void removeUser(Player player) {
        users.remove(player.getUniqueId());
    }

    public User getUser(Player player) {
        User user = users.get(player.getUniqueId());

        if (user != null) {
            return user;
        }

        return this.addUser(player);
    }

    public Set<User> getUsers() {
        return Set.copyOf(users.values());
    }

    public AbstractDatabase getDatabase() {
        return database;
    }
}
