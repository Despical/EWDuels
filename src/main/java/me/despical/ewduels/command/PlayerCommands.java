package me.despical.ewduels.command;

import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.annotations.Command;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class PlayerCommands extends AbstractCommandHandler {

    @Command(
        name = "ew"
    )
    public void mainCommand(CommandArguments arguments) {
        arguments.sendMessage("sa");
    }

    @Command(
        name = "ew.join",
        usage = "/ew join <arena_name>",
        desc = "Creates a join attempt for the arena with the given ID.",
        min = 1,
        senderType = Command.SenderType.PLAYER
    )
    public void join(User user) {
        Arena arena = plugin.getArenaRegistry().getArena(user);

        if (arena == null) {
            user.sendRawMessage("no arena with that name");
            return;
        }

        plugin.getArenaManager().joinAttempt(user, arena);
    }

    @Command(
        name = "ew.leave",
        usage = "/ew leave",
        desc = "Leaves the current arena if the player is in one.",
        senderType = Command.SenderType.PLAYER
    )
    public void leave(User user) {
        Arena arena = plugin.getArenaRegistry().getArena(user);

        if (arena == null) {
            user.sendRawMessage("&cYou are not playing EW Duels at the moment!");
            return;
        }

        plugin.getArenaManager().leaveAttempt(user, arena);
    }
}
