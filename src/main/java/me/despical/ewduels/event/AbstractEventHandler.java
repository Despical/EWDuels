package me.despical.ewduels.event;

import me.despical.ewduels.Main;
import org.bukkit.event.Listener;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public abstract class AbstractEventHandler implements Listener {

    protected static final Main plugin = Main.getPlugin(Main.class);

    public AbstractEventHandler() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
