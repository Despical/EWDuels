package me.despical.ewduels.handler.chat;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.util.Strings;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.Team;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ChatManager {

    private FileConfiguration config;
    private List<String> breakPlaceholder;

    private final EWDuels plugin;
    private final Map<String, String> cachedMessages;

    public ChatManager(EWDuels plugin) {
        this.plugin = plugin;
        this.cachedMessages = new HashMap<>();

        reload();
    }

    public void reload() {
        config = ConfigUtils.getConfig(plugin, "messages");
        breakPlaceholder = config.getStringList("placeholders.breaks");
    }

    public String getBreak(int score) {
        return breakPlaceholder.get(score - 1);
    }

    public String getMessage(String path) {
        String message = cachedMessages.get(path);

        if (message == null) {
            if (config.isList(path)) {
                message = Utils.listToString(config.getStringList(path));
            } else {
                message = config.getString(path);
            }

            cachedMessages.put(path, message);
        }

        return Strings.format(message);
    }

    public String getFormattedMessage(String path, Object... params) {
        String message = cachedMessages.get(path);

        if (message == null) {
            if (config.isList(path)) {
                message = Utils.listToString(config.getStringList(path));
            } else {
                message = config.getString(path);
            }

            cachedMessages.put(path, message);
        }

        message = Strings.format(message);
        return MessageFormat.format(message, params);
    }

    public String getFormattedRawMessage(String message, Object... params) {
        message = Strings.format(message);
        message = MessageFormat.format(message, params);
        return message;
    }

    public String getTeamColor(Team team) {
        return this.getMessage("placeholders.team-colors." + (team == Team.BLUE ? "blue" : "red"));
    }

    public String getTeamColor(User user) {
        return this.getTeamColor(user.getTeam());
    }

    public String getTeamColoredName(User user) {
        return this.getTeamColor(user) + user.getName();
    }

    public String getTeamColoredBoldName(User user) {
        return this.getTeamColor(user) + "&l" + user.getName();
    }

    public String getColoredScore(Arena arena, Team team) {
        return this.getTeamColor(team) + "&l" + arena.getPlayer(team).getStat(StatisticType.LOCAL_SCORE);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
}
