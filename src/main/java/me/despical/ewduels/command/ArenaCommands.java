package me.despical.ewduels.command;

import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.annotations.Command;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaCommands extends AbstractCommandHandler {

    // TODO - Move command messages to messages.yml


    @Command(
        name = "test"
    )
    public void test(CommandArguments arguments) {
        var itemManager = plugin.getItemManager();
        var items = List.of("lobby", "end", "first-player", "second-player", "first-egg", "second-egg", "save-and-exit");
        Player player = arguments.getSender();
        var inventory = player.getInventory();

        for (var specialItem : items) {
            var item = itemManager.getItem(specialItem);
            int slot = item.<Integer>getCustomKey("slot");

            inventory.setItem(slot, item.getItemStack());
        }
    }

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
            user.sendRawMessage("arena is already created");
            return;
        }

        plugin.getArenaRegistry().createArena(id);

        user.sendRawMessage("new arena instance created: " + id);
    }

    @Command(
        name = "ew.delete",
        permission = "ew.delete",
        usage = "/ew delete <arena_name>",
        desc = "Deletes the arena instance with the given ID, if it exists.",
        min = 1,
        senderType = Command.SenderType.PLAYER
    )
    public void delete(CommandArguments arguments, User user) {
        String id = arguments.getArgument(0);

        if (!plugin.getArenaRegistry().isArena(id)) {
            user.sendRawMessage("no arena is created");
            return;
        }

        plugin.getArenaRegistry().deleteArena(id);

        user.sendRawMessage("arena instance successfully deleted");
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
            user.sendRawMessage("no arenas created");
            return;
        }

        String list = arenas.stream().map(Arena::getId).collect(Collectors.joining(", "));

        user.sendRawMessage("arena list: " + list);
    }
}
