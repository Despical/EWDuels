package me.despical.ewduels.arena;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.entity.Player;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaManager {

    private final ArenaRegistry arenaRegistry;
    private final ChatManager chatManager;

    private User queuePlayer;

    public ArenaManager(EWDuels plugin) {
        this.arenaRegistry = plugin.getArenaRegistry();
        this.chatManager = plugin.getChatManager();
    }

    public void joinQueue(User player) {
        if (this.queuePlayer == player) {
            player.sendMessage("queue-messages.already-in-queue");
            return;
        }

        if (this.queuePlayer != null) {
            queuePlayer.sendMessage("queue-messages.opponent-found");
            player.sendMessage("queue-messages.player-two-match-starting");
            startGameFromQueue(player);
            return;
        }

        Arena arena = arenaRegistry.getRandomAvailableArena();
        if (arena == null) {
            player.sendMessage("queue-messages.no-arena-available");
            return;
        }

        this.queuePlayer = player;
        this.queuePlayer.sendMessage("queue-messages.joined");
    }

    public void leaveQueue(User player) {
        if (!this.queuePlayer.equals(player)) {
            player.sendMessage("queue-messages.not-in-queue");
            return;
        }

        this.queuePlayer = null;
        player.sendMessage("queue-messages.left");
    }

    // P1 = queuePlayer - P2 = otherPlayer
    public void startGameFromQueue(User otherPlayer) {
        Arena arena = arenaRegistry.getRandomAvailableArena();

        if (arena == null) {
            return; // TODO: HANDLE THIS
        }

        arena.addPlayer(queuePlayer);
        arena.addPlayer(otherPlayer);

        this.queuePlayer = null;
        arena.start();
    }

    public void endGame(Arena arena, User winner, User loser) {
        arena.stop();
        String message = chatManager.getFormattedMessage("game-messages.ended", winner.getName(), loser.getName());

        arena.getPlayers().forEach(player -> {
            player.sendRawMessage(message);
            player.sendRawMessage(message);

            player.teleport(arena.getLocation(GameLocation.LOBBY));
        });
    }

    public Arena getArenaOf(User user) {
        if (!user.isInMatch()) return null;

        for (Arena arena : arenaRegistry.getArenas()) {
            if (arena.isInArena(user)) {
                return arena;
            }
        }

        return null;
    }

    public User getQueuePlayer() {
        return queuePlayer;
    }

}
