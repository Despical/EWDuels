package me.despical.ewduels.event;

import me.despical.commons.compat.XMaterial;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaState;
import me.despical.ewduels.user.User;
import me.despical.fileitems.SpecialItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class InGameEvents extends AbstractEventHandler {

    @EventHandler
    public void onEggClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        User user = plugin.getUserManager().getUser(event.getPlayer());
        Arena arena = user.getArena();

        if (arena == null || !arena.isArenaState(ArenaState.IN_GAME)) {
            return;
        }

        if (block.getType() == XMaterial.DRAGON_EGG.parseMaterial()) {
            event.setCancelled(true);

            arena.handleNewPoint(user);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());
        Arena arena = user.getArena();

        if (arena == null) {
            return;
        }

        if (!arena.isArenaState(ArenaState.IN_GAME)) {
            event.setCancelled(true);
            return;
        }

        arena.handlePlacingBlocks(event.getBlock());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());
        Arena arena = user.getArena();

        if (arena == null) {
            return;
        }

        if (!arena.isArenaState(ArenaState.IN_GAME) || !arena.canBreak(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeaveQueue(PlayerInteractEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());

        if (user.getArena() == null) {
            return;
        }

        SpecialItem item = plugin.getItemManager().getItem("leave-queue");

        if (!item.equals(event.getItem())) {
            return;
        }

        plugin.getArenaManager().leaveQueue(user);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        User user = plugin.getUserManager().getUser(player);

        if (user.getArena() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());

        if (user.getArena() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());

        if (user.getArena() != null) {
            event.setCancelled(true);
        }
    }
}
