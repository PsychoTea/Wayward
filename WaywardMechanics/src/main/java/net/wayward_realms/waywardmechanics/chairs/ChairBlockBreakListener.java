package net.wayward_realms.waywardmechanics.chairs;

import net.wayward_realms.waywardmechanics.WaywardMechanics;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChairBlockBreakListener implements Listener {

    private WaywardMechanics plugin;

    public ChairBlockBreakListener(WaywardMechanics plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority= EventPriority.HIGHEST,ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (plugin.getChairManager().isBlockOccupied(block)) {
            Player player = plugin.getChairManager().getPlayerOnChair(block);
            plugin.getChairManager().unsitPlayerForce(player);
        }
    }

}
