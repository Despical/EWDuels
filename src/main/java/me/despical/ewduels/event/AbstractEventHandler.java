package me.despical.ewduels.event;

import me.despical.ewduels.EWDuels;
import org.bukkit.event.Listener;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public abstract class AbstractEventHandler implements Listener {

    protected static final EWDuels plugin = EWDuels.getPlugin(EWDuels.class);

    public AbstractEventHandler() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
