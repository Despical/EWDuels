package me.despical.ewduels.arena.scoreboard;

import me.despical.commons.scoreboard.ScoreboardLib;
import me.despical.commons.scoreboard.common.EntryBuilder;
import me.despical.commons.scoreboard.type.Entry;
import me.despical.commons.scoreboard.type.Scoreboard;
import me.despical.commons.scoreboard.type.ScoreboardHandler;
import me.despical.commons.string.StringFormatUtils;
import me.despical.commons.util.Strings;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.arena.ArenaState;
import me.despical.ewduels.arena.Team;
import me.despical.ewduels.handler.chat.ChatManager;
import me.despical.ewduels.option.Option;
import me.despical.ewduels.user.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 14.12.2024
 */
public class ScoreboardManager {

    private static final String date = StringFormatUtils.formatToday();

    private final EWDuels plugin;
    private final Arena arena;
    private final ChatManager chatManager;
    private final Map<User, Scoreboard> scoreboards;

    public ScoreboardManager(EWDuels plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.chatManager = plugin.getChatManager();
        this.scoreboards = new HashMap<>();
    }

    public void createScoreboard(User user) {
        if (!plugin.isEnabled(Option.SCOREBOARD_ENABLED)) {
            return;
        }

        user.cacheScoreboard();

        Player player = user.getPlayer();
        Scoreboard scoreboard = ScoreboardLib.createScoreboard(player).setHandler(new ScoreboardHandler() {

            @Override
            public String getTitle(Player player) {
                return chatManager.getMessage("scoreboard.title");
            }

            @Override
            public List<Entry> getEntries(Player player) {
                return formatScoreboard(player);
            }
        });

        scoreboard.activate();
        scoreboards.put(user, scoreboard);
    }

    public void removeScoreboard(User user) {
        if (!plugin.isEnabled(Option.SCOREBOARD_ENABLED)) {
            return;
        }

        Scoreboard scoreboard = scoreboards.get(user);

        if (scoreboard != null) {
            scoreboard.deactivate();

            user.removeScoreboard();

            scoreboards.remove(user);
        }
    }

    private List<Entry> formatScoreboard(Player player) {
        EntryBuilder builder = new EntryBuilder();
        List<String> lines = chatManager.getStringList("scoreboard.content." + arena.getArenaState().getDefaultName());

        User user = plugin.getUserManager().getUser(player);

        for (String line : lines) {
            String formattedLine = formatScoreboardLine(line, user);

            builder.next(formattedLine);
        }

        return builder.build();
    }

    private String formatScoreboardLine(String line, User user) {
        line = line.replace("%date%", date);
        line = line.replace("%version%", "v" + plugin.getDescription().getVersion());
        line = line.replace("%timer%", Integer.toString(arena.getTimer()));
        line = line.replace("%formatted_timer%", StringFormatUtils.formatIntoMMSS(arena.getRoundTimer()));
        line = line.replace("%map_name%", "Testinho");
        line = line.replace("%players%", Integer.toString(arena.getPlayers().size()));
        line = line.replace("%kills%", Integer.toString(user.getStat(StatisticType.LOCAL_KILL)));
        line = line.replace("%deaths%", Integer.toString(user.getStat(StatisticType.LOCAL_DEATH)));
        line = line.replace("%score%", Integer.toString(arena.getScore(user.getTeam())));
        line = line.replace("%win_streak%", Integer.toString(user.getStat(StatisticType.WIN_STREAK)));

        if (arena.isArenaState(ArenaState.IN_GAME, ArenaState.ENDING)) {
            line = line.replace("%blue_score%", getFormattedScore(Team.BLUE));
            line = line.replace("%red_score%", getFormattedScore(Team.RED));
        }

        return Strings.format(line);
    }

    private String getFormattedScore(Team team) {
        int score = arena.getScore(team);
        int targetScore = plugin.<Integer>getOption(Option.POINTS_TO_WIN);

        String color = chatManager.getTeamColor(team);
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= targetScore; i++) {
            if (i <= score) {
                result.append(color);
            } else result.append("&f");

            result.append("⬤");
        }

        return result.toString();
    }
}
