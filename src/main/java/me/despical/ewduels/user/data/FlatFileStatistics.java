package me.despical.ewduels.user.data;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.api.statistic.StatisticType;
import me.despical.ewduels.user.User;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public non-sealed class FlatFileStatistics extends AbstractDatabase {

    private final FileConfiguration config;

    public FlatFileStatistics(EWDuels plugin) {
        super(plugin);
        this.config = ConfigUtils.getConfig(plugin, "stats");
    }

    @Override
    public void saveStatistic(User user, StatisticType statisticType) {
        config.set(user.getUniqueId().toString() + "." + statisticType.getName(), user.getStat(statisticType));

        ConfigUtils.saveConfig(plugin, config, "stats");
    }

    @Override
    public void saveStatistics(User user) {
        String uuid = user.getUniqueId().toString();

        for (StatisticType stat : StatisticType.PERSISTENT_STATS) {
            config.set(uuid + "." + stat.getName(), user.getStat(stat));
        }

        ConfigUtils.saveConfig(plugin, config, "stats");
    }

    @Override
    public void saveAllStatistics() {
        for (User user : plugin.getUserManager().getUsers()) {
            String uuid = user.getUniqueId().toString();

            for (StatisticType stat : StatisticType.values()) {
                config.set(uuid + "." + stat.getName(), user.getStat(stat));
            }
        }

        ConfigUtils.saveConfig(plugin, config, "stats");
    }

    @Override
    public void loadStatistics(User user) {
        String uuid = user.getUniqueId().toString();

        for (StatisticType stat : StatisticType.values()) {
            user.setStat(stat, config.getInt(uuid + "." + stat.getName()));
        }
    }

    @Override
    public void shutdown() {
        this.saveAllStatistics();
    }
}
