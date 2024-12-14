package me.despical.ewduels.event;

import me.despical.commons.compat.XMaterial;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaState;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.User;
import me.despical.fileitems.SpecialItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

        Location location = block.getLocation();
        Location playerEggLocation = arena.getLocation(user.getTeam().getEggLocation());

        if (playerEggLocation.equals(location)) {
            event.setCancelled(true);

            user.sendMessage("game-messages.dont-break-your-egg");
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

        Location location = event.getBlock().getLocation();
        Location eggLocation = arena.getLocation(user.getTeam().getEggLocation());
        int radius = plugin.<Integer>getOption(Option.EGG_PROTECTION_RADIUS);

        if (location.distance(eggLocation) < radius) {
            event.setCancelled(true);

            user.sendMessage("game-messages.cant-place-here");
            return;
        }

        user.addStat(StatisticType.LOCAL_PLACED_BLOCKS, 1);

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
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());

        if (user.getArena() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damagerPlayer)) return;
        if (!(event.getEntity() instanceof Player victimPlayer)) return;

        User damager = plugin.getUserManager().getUser(damagerPlayer);
        User victim = plugin.getUserManager().getUser(victimPlayer);

        Arena arena = damager.getArena();

        if (arena == null || !arena.equals(victim.getArena())) {
            return;
        }

        if (!arena.isArenaState(ArenaState.IN_GAME)) {
            event.setCancelled(true);
            return;
        }

        double health = victimPlayer.getHealth();
        double finalDamage = event.getFinalDamage();

        damager.addStat(StatisticType.LOCAL_DAMAGE, (int) Math.ceil(finalDamage));

        if (health - finalDamage > 0) {
            return;
        }

        event.setCancelled(true);

        arena.handleDeath(victim, damager);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        User user = plugin.getUserManager().getUser(player);
        Arena arena = user.getArena();

        if (arena == null) {
            return;
        }

        switch (event.getCause()) {
            case VOID -> {
                if (arena.isArenaState(ArenaState.IN_GAME)) {
                    arena.resetPlayerPosition(user);
                    arena.broadcastMessage("game-messages.fell-into-void", user.getName());
                    return;
                }

                arena.teleportToStart(user);
            }

            case FALL -> {
                if (!arena.isArenaState(ArenaState.IN_GAME)) {
                    event.setCancelled(true);
                } else if (!plugin.isEnabled(Option.FALL_DAMAGE_ENABLED)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
