package me.despical.ewduels.user;

import me.despical.ewduels.Main;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class User {

    private static final Main plugin = Main.getPlugin(Main.class);

    private final UUID uuid;
    private final String name;

    public User(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public void sendRawMessage(String message, Object... params) {
        message = plugin.getChatManager().getFormattedRawMessage(message, params);

        this.getPlayer().sendMessage(message);
    }
}
