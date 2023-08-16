package org.playerdeath.machines;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

import java.util.Map;

public class MachineGUICommand implements Listener, CommandExecutor {
    private final JavaPlugin plugin;

    public MachineGUICommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        openMachineSelectionGUI(player);

        return true;
    }

    private void openMachineSelectionGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Select a Machine");

        Map<String, CustomMachine> machines = MachineManager.getAllMachines();
        for (CustomMachine machine : machines.values()) {
            inv.addItem(new ItemStack(machine.getDisplayItem()));
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = player.getOpenInventory().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (title.equals("Select a Machine")) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                openEditRecipeGUI(player, MachineManager.getMachine(clickedItem.getType().name()));
            }
        } else if (title.contains("Edit Recipe: ")) {
            event.setCancelled(true); // Prevent taking items out
            if (event.isShiftClick() && player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) {
                clickedItem.setType(player.getItemOnCursor().getType());
            }
        }

    }

    private void openEditRecipeGUI(Player player, CustomMachine machine) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.DISPENSER, "Edit Recipe: " + machine.getName());

        ItemStack[][] recipe = machine.getRecipe();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inv.setItem(i * 3 + j, recipe[i][j]);
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = player.getOpenInventory().getTitle();
        Inventory inv = event.getInventory();

        if (title.contains("Edit Recipe: ")) {
            CustomMachine machine = MachineManager.getMachine(title.replace("Edit Recipe: ", ""));
            ItemStack[][] newRecipe = new ItemStack[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newRecipe[i][j] = inv.getItem(i * 3 + j);
                }
            }
            machine.setRecipe(newRecipe);
            saveRecipeToConfig(machine.getName(), newRecipe);

        }
    }
    private void saveRecipeToConfig(String machineName, ItemStack[][] recipe) {
        File configFile = new File(plugin.getDataFolder(), "machines.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (int i = 0; i < 3; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                row.append(recipe[i][j].getType().name());
                if (j != 2) row.append(", ");
            }
            config.set("machines." + machineName + ".recipe." + i, row.toString());
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
