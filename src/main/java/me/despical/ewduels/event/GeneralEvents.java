package me.despical.ewduels.event;

import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class GeneralEvents extends AbstractEventHandler {

    private final Map<UUID, Arena> teleportToEnd = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getUserManager().addUser(player);

        Arena arena = teleportToEnd.get(player.getUniqueId());

        if (arena != null) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.teleport(arena.getLocation(GameLocation.END));

                InventorySerializer.loadInventory(plugin, player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        User user = plugin.getUserManager().getUser(player);

        Optional.ofNullable(user.getArena()).ifPresent(arena -> {
            teleportToEnd.put(player.getUniqueId(), arena);

            arena.handleQuit(user);
        });

        plugin.getUserManager().removeUser(player);
    }
}
