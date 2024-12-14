package me.despical.ewduels.handler.chat;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.string.StringFormatUtils;
import me.despical.commons.util.Strings;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaState;
import me.despical.ewduels.arena.Team;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

        Stream.of(ArenaState.values()).forEach(arenaState -> arenaState.setFormattedName(this.getMessage(arenaState.getPath())));
    }

    public void reload() {
        config = ConfigUtils.getConfig(plugin, "messages");
        breakPlaceholder = config.getStringList("placeholders.breaks");

        StringFormatUtils.setTimeFormat(this.getMessage("scoreboard.placeholders.timer-format"));
        StringFormatUtils.setDateFormat(this.getMessage("scoreboard.placeholders.date-format"));
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

    public String getTeamNameBold(User user) {
        String teamName = this.getTeamColor(user) + "&l" + this.getMessage("placeholders.team." + (user.getTeam() == Team.BLUE ? "blue" : "red"));;
        return Strings.format(teamName);
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

    public String getListElement(String path, int position) {
        List<String> list = config.getStringList(path);

        if (position >= list.size()) {
            return "";
        }

        return Strings.format(list.get(position));
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<String> getSummaryMessage(User winner, User loser) {
        Arena arena = winner.getArena();

        return config.getStringList("game-messages.summary").stream()
            .map(msg -> {
                msg = msg.replace("%formatted_time%", StringFormatUtils.formatIntoMMSS(arena.getRoundTimer()));
                msg = msg.replace("%winner_name%", winner.getName());
                msg = msg.replace("%winner_score%", Integer.toString(winner.getStat(StatisticType.LOCAL_SCORE)));
                msg = msg.replace("%winner_damage%", Integer.toString(winner.getStat(StatisticType.LOCAL_DAMAGE) / 2));
                msg = msg.replace("%winner_placed_blocks%", Integer.toString(winner.getStat(StatisticType.LOCAL_PLACED_BLOCKS)));

                msg = msg.replace("%loser_name%", loser.getName());
                msg = msg.replace("%loser_score%", Integer.toString(loser.getStat(StatisticType.LOCAL_SCORE)));
                msg = msg.replace("%loser_damage%", Integer.toString(loser.getStat(StatisticType.LOCAL_DAMAGE) / 2));
                msg = msg.replace("%loser_placed_blocks%", Integer.toString(loser.getStat(StatisticType.LOCAL_PLACED_BLOCKS)));
                return msg;
            }).toList();
    }
}
