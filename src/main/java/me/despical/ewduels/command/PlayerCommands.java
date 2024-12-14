package me.despical.ewduels.command;

import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.annotations.Command;
import me.despical.commandframework.annotations.Completer;
import me.despical.ewduels.user.User;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

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

    @Completer(
        name = "ew"
    )
    public List<String> tabCompletion(CommandArguments arguments) {
        List<String> emptyList = new ArrayList<>();

        if (arguments.isArgumentsEmpty()) {
            return null;
        }

        String arg = arguments.getArgument(0);

        if (!arguments.hasPermission("ew.tabcompleter")) {
            return StringUtil.copyPartialMatches(arg, List.of("join", "leave"), emptyList);
        }

        if ("delete".equals(arg) || "edit".equals(arg)) {
            return StringUtil.copyPartialMatches(arg, List.copyOf(plugin.getArenaRegistry().getArenaIds()), emptyList);
        }

        return StringUtil.copyPartialMatches(arg, List.of("create", "delete", "list", "edit", "join", "leave"), emptyList);
    }
}
