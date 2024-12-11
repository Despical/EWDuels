package me.despical.ewduels.arena.setup;

import me.despical.commons.compat.XMaterial;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import me.despical.fileitems.SpecialItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class SetupMode {

    private final EWDuels plugin;
    private final Arena arena;
    private final User user;
    private final Listener listener;

    public SetupMode(EWDuels plugin, Arena arena, User user) {
        this.plugin = plugin;
        this.arena = arena;
        this.user = user;
        this.listener = new SetupEvents();

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        InventorySerializer.saveInventoryToFile(plugin, user.getPlayer());

        Player player = user.getPlayer();
        player.getInventory().clear();

        List<String> itemNames = Stream.of(GameLocation.values()).map(GameLocation::getName).collect(Collectors.toList());
        itemNames.add("save-and-exit");

        for (String itemName : itemNames) {
            SpecialItem item = plugin.getItemManager().getItem(itemName);
            int slot = item.<Integer>getCustomKey("slot");

            player.getInventory().setItem(slot, item.getItemStack());
        }
    }

    public void exitSetup(boolean delete) {
        Player player = user.getPlayer();
        player.getInventory().clear();

        HandlerList.unregisterAll(listener);

        if (delete) {
            user.sendMessage("setup.arena-deleted");
            return;
        }

        boolean ready = true;

        for (GameLocation gameLocation : GameLocation.values()) {
            Location location = arena.getLocation(gameLocation);

            if (location == null) {
                ready = false;

                user.sendFormattedMessage("setup.missing-location", "sa");
                break;
            }
        }

        arena.setReady(ready);

        user.sendMessage("setup.exiting");
    }

    private class SetupEvents implements Listener {

        @EventHandler
        public void setLocations(PlayerInteractEvent event) {
            Player player = event.getPlayer();

            if (!player.getUniqueId().equals(user.getUniqueId())) {
                return;
            }

            for (GameLocation gameLocation : GameLocation.values()) {
                if (gameLocation.name().contains("EGG")) continue;

                SpecialItem item = plugin.getItemManager().getItem(gameLocation.getName());

                if (!item.equals(event.getItem())) {
                    continue;
                }

                event.setCancelled(true);

                Block clickedBlock = event.getClickedBlock();
                Location location = clickedBlock != null ? clickedBlock.getLocation() : player.getLocation();
                arena.setLocation(gameLocation, location);

                user.sendFormattedMessage("setup.location-set", gameLocation.getFormattedName());
                break;
            }
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            Player player = event.getPlayer();

            if (!player.getUniqueId().equals(user.getUniqueId())) {
                return;
            }

            for (GameLocation gameLocation : List.of(GameLocation.FIRST_EGG, GameLocation.SECOND_EGG)) {
                SpecialItem item = plugin.getItemManager().getItem(gameLocation.getName());

                if (!item.equals(event.getItemInHand())) {
                    continue;
                }

                Location eggLocation = arena.getLocation(gameLocation);

                if (eggLocation != null && eggLocation.getBlock().getType() == XMaterial.DRAGON_EGG.parseMaterial()) {
                    eggLocation.getBlock().setType(Material.AIR);
                }

                arena.setLocation(gameLocation, event.getBlock().getLocation());

                user.sendFormattedMessage("setup.location-set", gameLocation.getFormattedName());
                break;
            }
        }

        @EventHandler
        public void exitSetupMode(PlayerInteractEvent event) {
            Player player = event.getPlayer();

            if (!player.getUniqueId().equals(user.getUniqueId())) {
                return;
            }

            SpecialItem exitItem = plugin.getItemManager().getItem("save-and-exit");

            if (!exitItem.equals(event.getItem())) {
                return;
            }

            event.setCancelled(true);

            InventorySerializer.loadInventory(plugin, event.getPlayer());

            arena.endSetupSeason(false);
        }
    }
}
