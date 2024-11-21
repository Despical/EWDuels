package me.despical.ewduels.user;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class UserManager {

    private final Map<UUID, User> users;

    public UserManager() {
        this.users = new HashMap<>();
    }

    public User addUser(Player player) {
        User user = new User(player);
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
}
