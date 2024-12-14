package me.despical.ewduels.user;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.Team;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class User {

    private static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);

    private Team team;
    private Scoreboard cachedScoreboard;

    private final String name;
    private final UUID uuid;
    private final Map<StatisticType, Integer> stats;

    public User(Player player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.stats = new EnumMap<>(StatisticType.class);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void resetTemporaryStatistics() {
        for (StatisticType type : StatisticType.PERSISTENT_STATS) {
            stats.put(type, 0);
        }

        this.team = null;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void sendMessage(String path) {
        String message = plugin.getChatManager().getMessage(path);

        if ("%do_not_send%".equals(message)) return;

        getPlayer().sendMessage(message);
    }

    public void sendFormattedMessage(String path, Object... params) {
        String message = plugin.getChatManager().getFormattedMessage(path, params);

        getPlayer().sendMessage(message);
    }

    public void sendRawMessage(String message, Object... params) {
        message = plugin.getChatManager().getFormattedRawMessage(message, params);

        getPlayer().sendMessage(message);
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public Arena getArena() {
        return plugin.getArenaRegistry().getArena(this);
    }

    public int getStat(StatisticType statisticType) {
        return stats.computeIfAbsent(statisticType, stat -> 0);
    }

    public void setStat(StatisticType stat, int value) {
        stats.put(stat, value);
    }

    public void addStat(StatisticType stat, int value) {
        setStat(stat, getStat(stat) + value);
    }

    public void cacheScoreboard() {
        this.cachedScoreboard = getPlayer().getScoreboard();
    }

    public void removeScoreboard() {
        if (cachedScoreboard != null) {
            getPlayer().setScoreboard(cachedScoreboard);

            cachedScoreboard = null;
        }
    }
}
