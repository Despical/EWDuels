package me.despical.ewduels.event;

import me.despical.commons.compat.XMaterial;
import me.despical.ewduels.arena.Arena;
import me.despical.ewduels.user.User;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class InGameEvents extends AbstractEventHandler {

    @EventHandler
    public void onEggClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        User user = plugin.getUserManager().getUser(event.getPlayer());
        Arena arena = user.getArena();

        if (arena == null) {
            return;
        }

        if (block.getType() == XMaterial.DRAGON_EGG.parseMaterial()) {
            event.setCancelled(true);

        }
    }
}
