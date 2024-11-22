package me.despical.ewduels.command;

import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.annotations.Command;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaCommands extends AbstractCommandHandler {

    @Command(
        name = "ew.create",
        permission = "ew.create",
        usage = "/ew create <arena_name>",
        desc = "Creates an arena instance with the given ID.",
        min = 1,
        senderType = Command.SenderType.PLAYER
    )
    public void create(CommandArguments arguments, User user) {
        String id = arguments.getArgument(0);

        if (plugin.getArenaRegistry().isArena(id)) {
            user.sendMessage("admin-commands.arena-already-created");
            return;
        }

        Arena arena = plugin.getArenaRegistry().registerNewArena(id);
        arena.createSetupSeason(user);

        user.sendFormattedMessage("admin-commands.created-new-arena", id);
    }

    @Command(
        name = "ew.delete",
        permission = "ew.delete",
        usage = "/ew delete <arena_name>",
        desc = "Deletes the arena instance with the given ID.",
        min = 1,
        senderType = Command.SenderType.PLAYER
    )
    public void delete(CommandArguments arguments, User user) {
        String id = arguments.getArgument(0);

        if (!plugin.getArenaRegistry().isArena(id)) {
            user.sendFormattedMessage("admin-commands.no-arena-like-that", id);
            return;
        }

        plugin.getArenaRegistry().unregisterArena(id);

        user.sendFormattedMessage("admin-commands.deleted-arena", id);
    }

    @Command(
        name = "ew.list",
        permission = "ew.list",
        usage = "/ew list",
        desc = "Shows the list of existing arena IDs.",
        senderType = Command.SenderType.PLAYER
    )
    public void list(User user) {
        Set<Arena> arenas = plugin.getArenaRegistry().getArenas();

        if (arenas.isEmpty()) {
            user.sendMessage("admin-commands.no-arenas-created");
            return;
        }

        String list = arenas.stream()
            .map(Arena::getId)
            .collect(Collectors.joining(", "));

        user.sendFormattedMessage("admin-commands.arena-list", list);
    }

    @Command(
        name = "ew.edit",
        permission = "ew.edit",
        usage = "/ew edit <arena_name>",
        desc = "Gives the editor tools.",
        senderType = Command.SenderType.PLAYER
    )
    public void edit(CommandArguments arguments, User user) {
        String id = arguments.getArgument(0);
        Arena arena = plugin.getArenaRegistry().getArena(id);

        if (arena == null) {
            user.sendFormattedMessage("admin-commands.no-arena-like-that", id);
            return;
        }

        if (arena.isSetupMode()) {
            user.sendMessage("admin-commands.can-not-edit-now");
            return;
        }

        arena.createSetupSeason(user);
    }
}
