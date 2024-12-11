package me.despical.ewduels.arena;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.user.User;

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
        Arena arena = plugin.getArenaRegistry().getRandomAvailableArena();

        if (arena == null) {
            user.sendMessage("queue-messages.no-arena-available");
            return;
        }

        if (user.equals(queuePlayer)) {
            user.sendMessage("queue-messages.already-in-queue");
            return;
        }

        if (queuePlayer != null) {
            queuePlayer.sendMessage("queue-messages.opponent-found");
            user.sendMessage("queue-messages.player-two-match-starting");

            arena.addPlayer(user);

            queuePlayer = null;
            return;
        }

        queuePlayer = user;
        queuePlayer.sendMessage("queue-messages.joined");

        arena.addPlayer(user);
    }

    public void leaveQueue(User player) {
        if (!queuePlayer.equals(player)) {
            player.sendMessage("queue-messages.not-in-queue");
            return;
        }

        queuePlayer = null;

        player.sendMessage("queue-messages.left");
    }

    public User getQueuePlayer() {
        return queuePlayer;
    }
}
