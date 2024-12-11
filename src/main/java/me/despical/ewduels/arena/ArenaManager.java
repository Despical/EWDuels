package me.despical.ewduels.arena;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;

import java.util.stream.Stream;

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
        Arena arena = arenaRegistry.getRandomAvailableArena();

        if (arena == null) {
            player.sendMessage("queue-messages.no-arena-available");
            return;
        }

        if (player.equals(queuePlayer)) {
            player.sendMessage("queue-messages.already-in-queue");
            return;
        }

        if (queuePlayer != null) {
            queuePlayer.sendMessage("queue-messages.opponent-found");
            player.sendMessage("queue-messages.player-two-match-starting");

            arena.addPlayer(player);

            queuePlayer = null;
            return;
        }

        queuePlayer = player;
        queuePlayer.sendMessage("queue-messages.joined");
    }

    public void leaveQueue(User player) {
        if (!queuePlayer.equals(player)) {
            player.sendMessage("queue-messages.not-in-queue");
            return;
        }

        queuePlayer = null;

        player.sendMessage("queue-messages.left");
    }

//    public void endGame(Arena arena, User winner, User loser) {
//        arena.stop();
//        String message = chatManager.getFormattedMessage("game-messages.ended", winner.getName(), loser.getName());
//
//        arena.getPlayers().forEach(player -> {
//            player.sendRawMessage(message);
//            player.sendRawMessage(message);
//
//            player.teleport(arena.getLocation(GameLocation.LOBBY));
//        });
//    }

    public User getQueuePlayer() {
        return queuePlayer;
    }
}
