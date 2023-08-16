package org.playerdeath.machines;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Machines extends JavaPlugin {
    private Map<Location, BlockFace> detectedMiners = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DROPPER) {
            // Check for an adjacent chest
            for (BlockFace face : BlockFace.values()) {
                if (event.getBlock().getRelative(face).getType() == Material.CHEST) {
                    // Miner setup detected, store this setup in the Set for reference
                    detectedMiners.put(event.getBlock().getLocation(), face);
                }
            }
        }
    }
    private void saveMinersToConfig() {
        List<String> minerLocations = new ArrayList<>();

        for (Map.Entry<Location, BlockFace> entry : detectedMiners.entrySet()) {
            Location location = entry.getKey();
            minerLocations.add(location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + entry.getValue().name());
        }

        getConfig().set("miners", minerLocations);
        saveConfig();
    }

    private void loadMinersFromConfig() {
        List<String> minerLocations = getConfig().getStringList("miners");

        for (String serializedLocation : minerLocations) {
            String[] parts = serializedLocation.split(",");
            World world = Bukkit.getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            BlockFace face = BlockFace.valueOf(parts[4]);

            Location location = new Location(world, x, y, z);
            detectedMiners.put(location, face);
        }
    }


    @Override
    public void onEnable() {
        // Save the default configuration if it doesn't exist
        saveDefaultConfig();
        loadMinersFromConfig();

        // Initialize MachineManager and load machines from the config
        MachineManager.loadMachines(this);

        // Set command executor for the /machinegui command
        this.getCommand("machinegui").setExecutor(new MachineGUICommand(this));

        // Register the MachineCraftListener
        getServer().getPluginManager().registerEvents(new MachineCraftListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            // For each detected Miner setup
            for (Location minerLocation : detectedMiners.keySet()) {
                Block dropperBlock = minerLocation.getBlock();
                for (BlockFace face : BlockFace.values()) {
                    Block adjacentBlock = dropperBlock.getRelative(face);
                    if (adjacentBlock.getType() == Material.CHEST) {
                        Chest chest = (Chest) adjacentBlock.getState();
                        ItemStack item = getRandomOre();
                        chest.getInventory().addItem(item); // Consider checking for space before adding
                    }
                }
            }
        }, 0L, 20 * 20L);
        getServer().getPluginManager().registerEvents(new MinerPlaceListener(this), this);


    }
    public void addDetectedMiner(Location location, BlockFace face) {
        detectedMiners.put(location, face);
    }

    private ItemStack getRandomOre() {
        double chance = Math.random() * 100; // get a random number between 0-100

        if (chance < 80) {
            return new ItemStack(Material.COBBLESTONE);
        } else if (chance < 90) {
            return new ItemStack(Material.IRON_ORE);
        } else if (chance < 95) {
            return new ItemStack(Material.GOLD_ORE);
        } else if (chance < 98) {
            return new ItemStack(Material.DIAMOND_ORE);
        } else {
            return new ItemStack(Material.EMERALD_ORE);
        }
    }

    @Override
    public void onDisable() {
        saveMinersToConfig();
        // You can save configurations or handle other shutdown logic here if needed
    }
}
