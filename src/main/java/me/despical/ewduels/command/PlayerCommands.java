package me.despical.ewduels.command;

import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.annotations.Command;
import me.despical.commandframework.annotations.Completer;
import me.despical.commons.miscellaneous.MiscUtils;
import me.despical.commons.util.Strings;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaState;
import me.despical.ewduels.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        name = "ew",
        usage = "/ew",
        desc = "Main command of the plugin."
    )
    public void mainCommand(CommandArguments arguments) {
        if (arguments.isArgumentsEmpty()) {
            arguments.sendMessage("&3This server is running &bEW Duels {0} &3by &bDespical &3& &bbilektugrul&3.", plugin.getDescription().getVersion());

            if (arguments.hasPermission("ew.admin")) {
                arguments.sendMessage("&3Commands: &b/{0} help", arguments.getLabel());
            }

            return;
        }

        arguments.sendMessage("&cUnrecognized command: /{0} {1}", arguments.getLabel(), arguments.concatArguments());
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
        Arena arena = user.getArena();

        if (arena != null && arena.isArenaState(ArenaState.IN_GAME)) {
            arena.handleLeave(user);
            return;
        }

        plugin.getArenaManager().leaveQueue(user);
    }

    @SuppressWarnings("deprecation")
    @Command(
        name = "ew.help",
        usage = "/ew help",
        desc = "Displays a list of available commands along with their descriptions.",
        permission = "ew.help"
    )
    public void helpCommand(CommandArguments arguments) {
        boolean isPlayer = arguments.isSenderPlayer();
        CommandSender sender = arguments.getSender();

        arguments.sendMessage("");
        MiscUtils.sendCenteredMessage(arguments.getSender(), "&3&lEW Duels");
        MiscUtils.sendCenteredMessage(arguments.getSender(), "&3[&boptional argument&3] &b- &3<&brequired argument&3>");
        arguments.sendMessage("");

        for (Command command : plugin.getCommandFramework().getSubCommands()) {
            String desc = command.desc(), usage = command.usage();

            if (desc.isEmpty()) continue;

            if (isPlayer) {
                ((Player) sender).spigot().sendMessage(
                    new ComponentBuilder(ChatColor.DARK_GRAY + " • ")
                        .append(formatCommandUsage("&3" + usage))
                        .color(ChatColor.AQUA)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(desc)))
                        .create());
            } else {
                sender.sendMessage(Strings.format(" &8• &b" + formatCommandUsage("&3" + usage) + " &3- &b" + desc));
            }
        }

        if (isPlayer) {
            Player player = arguments.getSender();
            player.sendMessage("");
            player.spigot().sendMessage(new ComponentBuilder("TIP:").color(ChatColor.YELLOW).bold(true)
                .append(" Try to ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                .append("hover").color(ChatColor.WHITE).underlined(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Hover on the commands to get info about them.")))
                .append(" or ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                .append("click").color(ChatColor.WHITE).underlined(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Click on the commands to insert them in the chat.")))
                .append(" on the commands!", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                .create());
        }
    }

    @Completer(
        name = "ew"
    )
    public List<String> tabCompletion(CommandArguments arguments) {
        List<String> emptyList = new ArrayList<>();
        String arg = arguments.getArgument(0);

        if (!arguments.hasPermission("ew.tabcompleter")) {
            return StringUtil.copyPartialMatches(arg, List.of("join", "leave"), emptyList);
        }

        if (arguments.getLength() == 2) {
            if ("delete".equals(arg) || "edit".equals(arg)) {
                return StringUtil.copyPartialMatches(arguments.getArgument(1), plugin.getArenaRegistry().getArenaIds(), emptyList);
            }
        }

        if (arguments.getLength() == 1) {
            return StringUtil.copyPartialMatches(arg, List.of("create", "delete", "list", "edit", "join", "leave", "help"), emptyList);
        }

        return emptyList;
    }

    private String formatCommandUsage(String usage) {
        char[] array = usage.toCharArray();
        StringBuilder buffer = new StringBuilder(usage);

        for (int i = 0; i < array.length; i++) {
            if (array[i] == '[' || array[i] == '<') {
                buffer.insert(i, "&b");
                return Strings.format(buffer.toString());
            }
        }

        return Strings.format(usage);
    }
}
