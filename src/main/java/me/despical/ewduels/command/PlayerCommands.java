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
        usage = "/ew join",
        desc = "Joins the queue and awaits opponents.",
        min = 1,
        senderType = Command.SenderType.PLAYER
    )
    public void join(User user) {
        plugin.getArenaManager().joinQueue(user);
    }

    @Command(
        name = "ew.leave",
        usage = "/ew leave",
        desc = "Leaves the queue if player is in one.",
        senderType = Command.SenderType.PLAYER
    )
    public void leave(User user) {
        plugin.getArenaManager().leaveQueue(user);
    }
}
