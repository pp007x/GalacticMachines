package org.playerdeath.machines;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MinerPlaceListener implements Listener {

    private Machines plugin;

    public MinerPlaceListener(Machines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DROPPER) {
            // Check for an adjacent chest
            for (BlockFace face : BlockFace.values()) {
                if (event.getBlock().getRelative(face).getType() == Material.CHEST) {
                    // Miner setup detected, store this setup in the Map for reference
                    plugin.addDetectedMiner(event.getBlock().getLocation(), face);
                }
            }
        }
    }
}
