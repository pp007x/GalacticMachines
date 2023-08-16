package org.playerdeath.machines;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loadMachines {
    public static Map<String, CustomMachine> machines = new HashMap<>();

    public static void loadMachines(JavaPlugin plugin) {
        ConfigurationSection machineSection = plugin.getConfig().getConfigurationSection("machines");
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

}
