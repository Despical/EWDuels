package me.despical.ewduels.arena;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.ewduels.Main;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaRegistry {

    private final Main plugin;
    private final FileConfiguration config;
    private final Map<String, Arena> arenas;

    public ArenaRegistry(Main plugin) {
        this.plugin = plugin;
        this.config = ConfigUtils.getConfig(plugin, "arena");
        this.arenas = new HashMap<>();

        loadArenas();
    }

    public Arena getArena(User user) {
        return arenas.values()
            .stream()
            .filter(arena -> arena.isInArena(user))
            .findFirst()
            .orElse(null);
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }

    public boolean isArena(String id) {
        return arenas.containsKey(id);
    }

    public boolean isInArena(User user) {
        return arenas.values()
            .stream()
            .anyMatch(arena -> arena.isInArena(user));
    }

    public Set<Arena> getArenas() {
        return Set.copyOf(arenas.values());
    }

    public Arena registerNewArena(String id) {
        Arena arena = new Arena(id);

        saveData(arena);

        arenas.put(id, arena);
        return arena;
    }

    public void unregisterArena(String id) {
        Arena arena = arenas.get(id);

        if (arena.isSetupMode()) {
            arena.endSetupSeason(true);
        }

        config.set("arenas." + id, null);

        arenas.remove(id);
    }

    public void saveData() {
        ConfigUtils.saveConfig(plugin, config, "arena");
    }

    private void loadArenas() {
        ConfigurationSection section = config.getConfigurationSection("arenas");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {
            String path = "arenas.%s.".formatted(id);

            Arena arena = new Arena(id);
            arena.setReady(config.getBoolean(path + "ready"));

            for (GameLocation gameLocation : GameLocation.values()) {
                Location location = LocationSerializer.fromString(path + gameLocation.getName());

                arena.setLocation(gameLocation, location);
            }

            arenas.put(id, arena);
        }
    }

    private void saveData(Arena arena) {
        String path = "arenas.%s.".formatted(arena.getId());

        for (GameLocation gameLocation : GameLocation.values()) {
            String location = LocationSerializer.toString(arena.getLocation(gameLocation));

            config.set(path + gameLocation.getName(), location);
        }

        config.set(path + "ready", arena.isReady());
    }
}
