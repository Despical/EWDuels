package me.despical.ewduels.event;

import me.despical.ewduels.Main;
import org.bukkit.event.Listener;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public abstract class AbstractEventHandler implements Listener {

    protected final Main plugin;

    public AbstractEventHandler(Main plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
