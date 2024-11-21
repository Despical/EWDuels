package me.despical.ewduels.arena;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.ewduels.Main;
import me.despical.ewduels.user.User;
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

    private final FileConfiguration config;
    private final Map<String, Arena> arenas;

    public ArenaRegistry(Main plugin) {
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

    public void createArena(String id) {
        Arena arena = new Arena(id);

        saveData(arena);

        arenas.put(id, arena);
    }

    public void deleteArena(String id) {
        config.set("arenas." + id, null);

        arenas.remove(id);
    }

    private void loadArenas() {
        ConfigurationSection section = config.getConfigurationSection("arenas");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {
            Arena arena = new Arena(id);

            arenas.put(id, arena);
        }
    }

    private void saveData(Arena arena) {
        String path = "arenas.%s.".formatted(arena.getId());

        config.set(path + "test", "null");
    }
}
