package me.despical.ewduels.arena;

import me.despical.commons.compat.XMaterial;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.ewduels.EWDuels;
import me.despical.ewduels.user.User;
import me.despical.ewduels.util.GameLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ArenaRegistry {

    private final EWDuels plugin;
    private final FileConfiguration config;
    private final Map<String, Arena> arenas;

    public ArenaRegistry(EWDuels plugin) {
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

    public Arena getRandomAvailableArena() {
        List<Arena> currentArenas = arenas.values().stream()
            .filter(arena -> arena.isReady() && !arena.isSetupMode())
            .filter(arena -> arena.isArenaState(ArenaState.WAITING) && arena.getPlayers().size() < 2)
            .collect(Collectors.toList());

        Collections.shuffle(currentArenas);
        return currentArenas.isEmpty() ? null : currentArenas.get(0);
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }

    public boolean isArena(String id) {
        return arenas.containsKey(id);
    }

    public Set<Arena> getArenas() {
        return Set.copyOf(arenas.values());
    }

    public List<String> getArenaIds() {
        return List.copyOf(arenas.keySet());
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

        arena.stop();

        config.set("arenas." + id, null);

        arenas.remove(id);
    }

    public void saveData() {
        for (Arena arena : arenas.values()) {
            saveData(arena);
        }

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
            arena.setMapName(config.getString(path + "mapName"));

            if (arena.isReady()) {
                arena.start();
            }

            Material dragonEgg = XMaterial.DRAGON_EGG.parseMaterial();

            for (GameLocation gameLocation : GameLocation.values()) {
                Location location = LocationSerializer.fromString(config.getString(path + gameLocation.getName()));

                arena.setLocation(gameLocation, location);

                switch (gameLocation) {
                    case FIRST_EGG, SECOND_EGG -> {
                        Block block = location.getBlock();

                        if (block.getType() != dragonEgg) {
                            block.setType(dragonEgg);
                        }
                    }
                }
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
        config.set(path + "mapName", arena.getMapName());
    }
}
