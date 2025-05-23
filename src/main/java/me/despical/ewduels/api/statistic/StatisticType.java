package me.despical.ewduels.api.statistic;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public enum StatisticType {

    WIN("win"),
    LOSE("lose"),
    KILL("kill"),
    DEATH("death"),
    GAMES_PLAYED("gamesplayed"),
    WIN_STREAK("winstreak"),
    LOCAL_DAMAGE,
    LOCAL_KILL,
    LOCAL_DEATH,
    LOCAL_PLACED_BLOCKS;

    public static final StatisticType[] PERSISTENT_STATS = {WIN, LOSE, KILL, DEATH, GAMES_PLAYED, WIN_STREAK};

    private final String name;
    private final boolean persistent;

    StatisticType(String name) {
        this.name = name;
        this.persistent = true;
    }

    StatisticType() {
        this.name = null;
        this.persistent = false;
    }

    public String getName() {
        return name;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public static StatisticType matchType(String name) {
        for (StatisticType type : PERSISTENT_STATS) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
