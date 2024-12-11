package me.despical.ewduels.user.data;

import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.user.User;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public abstract sealed class AbstractDatabase permits FlatFileStatistics {

    protected final EWDuels plugin;

    public AbstractDatabase(EWDuels plugin) {
        this.plugin = plugin;
    }

    public abstract void saveStatistic(User user, StatisticType statisticType);

    public abstract void saveStatistics(User user);

    public abstract void loadStatistics(User user);

    public abstract void saveAllStatistics();

    public abstract void shutdown();
}
