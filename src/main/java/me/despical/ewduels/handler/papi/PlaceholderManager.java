package me.despical.ewduels.handler.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.despical.commons.string.StringFormatUtils;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Despical
 * <p>
 * Created at 18.12.2024
 */
public class PlaceholderManager extends PlaceholderExpansion {

    private final EWDuels plugin;

    public PlaceholderManager(EWDuels plugin) {
        this.plugin = plugin;

        register();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "ew";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Despical";
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String id) {
        User user = plugin.getUserManager().getUser(player);

        if (id.startsWith("stat_")) {
            StatisticType type = StatisticType.matchType(id.substring(5));

            return type != null ? Integer.toString(user.getStat(type)) : null;
        }

        return handleArenaPlaceholderRequest(id);
    }

    private String handleArenaPlaceholderRequest(String id) {
        if (!id.contains(":")) return null;

        String[] data = id.split(":");
        Arena arena = plugin.getArenaRegistry().getArena(data[0]);

        if (arena == null) return null;

        return switch (data[1].toLowerCase()) {
            case "players" -> Integer.toString(arena.getPlayers().size());
            case "timer" -> StringFormatUtils.formatIntoMMSS(arena.getTimer());
            case "round_timer" -> StringFormatUtils.formatIntoMMSS(arena.getRoundTimer());
            case "unformatted_timer" -> Integer.toString(arena.getTimer());
            case "state" -> arena.getArenaState().name();
            case "state_pretty" -> arena.getArenaState().getFormattedName();
            case "map_name" -> arena.getMapName();
            default -> null;
        };
    }
}
