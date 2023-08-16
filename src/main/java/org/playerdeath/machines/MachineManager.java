package org.playerdeath.machines;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineManager {

    private static Map<String, CustomMachine> machines = new HashMap<>();

    public static void loadMachines(JavaPlugin plugin) {
        // Ensure there's a default machines.yml. This just copies the file if it doesn't exist.
        plugin.saveResource("machines.yml", false);

        // Load the custom config
        FileConfiguration machineConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "machines.yml"));

        ConfigurationSection machineSection = machineConfig.getConfigurationSection("machines");
        if (machineSection != null) {
            for (String machineName : machineSection.getKeys(false)) {
                Material displayItem = Material.valueOf(machineSection.getString(machineName + ".displayItem"));
                List<String> recipeList = machineSection.getStringList(machineName + ".recipe");
                ItemStack[][] recipe = new ItemStack[3][3];

                for (int i = 0; i < 3; i++) {
                    String[] row = recipeList.get(i).split(", ");
                    for (int j = 0; j < 3; j++) {
                        recipe[i][j] = new ItemStack(Material.valueOf(row[j]));
                    }
                }
                CustomMachine machine = new CustomMachine(machineName, displayItem, recipe);
                machines.put(machineName, machine);
            }
        }
    }

    public static CustomMachine getMachine(String name) {
        return machines.get(name);
    }

    public static Map<String, CustomMachine> getAllMachines() {
        return machines;
    }

    // More utility methods can be added as needed.
}

