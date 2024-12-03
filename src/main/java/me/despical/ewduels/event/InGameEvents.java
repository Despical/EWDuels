package me.despical.ewduels.event;

import me.despical.commons.compat.XMaterial;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class InGameEvents extends AbstractEventHandler {

    @EventHandler
    public void onEggClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        Player player = e.getPlayer();
        User user = plugin.getUserManager().getUser(player);
        if (!user.isInMatch()) {
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaOf(user);
        if (e.getClickedBlock().getType() == XMaterial.DRAGON_EGG.parseMaterial()) {
            arena.addScore(user);
        }
    }

}
