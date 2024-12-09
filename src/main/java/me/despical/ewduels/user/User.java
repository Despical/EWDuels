package me.despical.ewduels.user;

import me.despical.ewduels.EWDuels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class User {

    private static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);

    private final String name;
    private final UUID uuid;

    private UserState state = UserState.FREE;

    public User(Player player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    public boolean isInQueue() {
        return state == UserState.IN_QUEUE;
    }

    public boolean isInMatch() {
        return state == UserState.IN_MATCH || state == UserState.STARTING_MATCH;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void teleport(Location location) {
        getPlayer().teleport(location);
    }

    public void sendMessage(String path) {
        String message = plugin.getChatManager().getMessage(path);

        getPlayer().sendMessage(message);
    }

    public void sendFormattedMessage(String path, Object... params) {
        String message = plugin.getChatManager().getFormattedMessage(path, params);

        getPlayer().sendMessage(message);
    }

    public void sendRawMessage(String message, Object... params) {
        message = plugin.getChatManager().getFormattedRawMessage(message, params);

        getPlayer().sendMessage(message);
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
