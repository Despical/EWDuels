package me.despical.ewduels.option;

import me.despical.ewduels.EWDuels;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public class ConfigOptions {

    private final EWDuels plugin;
    private final Map<Option, Object> options;

    public ConfigOptions(EWDuels plugin) {
        this.plugin = plugin;
        this.options = new EnumMap<>(Option.class);

        loadOptions();
    }

    private void loadOptions() {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        for (Option option : Option.values()) {
            options.put(option, option.parse(config.get(option.path, option.defaultValue)));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getOption(Option option) {
        return (T) options.get(option);
    }
}
