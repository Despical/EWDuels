package me.despical.ewduels.arena;

import me.despical.commons.miscellaneous.AttributeUtils;
import me.despical.commons.serializer.InventorySerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import me.despical.fileitems.SpecialItem;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaManager {

    private final EWDuels plugin;

    private User queuePlayer;

    public ArenaManager(EWDuels plugin) {
        this.plugin = plugin;
    }

    public void joinQueue(User user) {
        if (user.equals(queuePlayer)) {
            user.sendMessage("queue-messages.already-in-queue");
            return;
        }

        Arena arena = plugin.getArenaRegistry().getRandomAvailableArena();

        if (queuePlayer == null && arena == null) {
            user.sendMessage("queue-messages.no-arena-available");
            return;
        }

        Player player = user.getPlayer();

        InventorySerializer.saveInventoryToFile(plugin, player);

        if (plugin.isEnabled(Option.CLEAR_INVENTORY_ON_QUEUE)) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }

        player.setFoodLevel(20);

        AttributeUtils.healPlayer(player);

        if (queuePlayer != null) {
            arena = queuePlayer.getArena();

            queuePlayer.sendMessage("queue-messages.opponent-found");
            user.sendMessage("queue-messages.player-two-match-starting");
            user.setTeam(Team.BLUE);

            arena.addPlayer(user);

            queuePlayer = null;
            return;
        }

        queuePlayer = user;
        queuePlayer.sendMessage("queue-messages.joined");

        if (plugin.isEnabled(Option.LEAVE_QUEUE_ITEM)) {
            SpecialItem item = plugin.getItemManager().getItem("leave-queue");

            player.getInventory().setItem(item.<Integer>getCustomKey("slot"), item.getItemStack());
        }

        user.setTeam(Team.RED);

        arena.addPlayer(user);
    }

    public void leaveQueue(User user) {
        Arena arena = plugin.getArenaRegistry().getArena(user);

        if (arena == null || !user.equals(queuePlayer)) {
            user.sendMessage("queue-messages.not-in-queue");
            return;
        }

        arena.removePlayer(user);

        queuePlayer = null;
        user.sendMessage("queue-messages.left");
        user.resetTemporaryStatistics();

        Player player = user.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (plugin.isEnabled(Option.SEND_TO_END_ON_QUEUE_LEAVE)) {
            player.teleport(arena.getLocation(GameLocation.END));
        }

        InventorySerializer.loadInventory(plugin, player);
    }
}
