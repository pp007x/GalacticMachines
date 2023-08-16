package org.playerdeath.machines;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MachineCraftListener implements Listener {
    private final JavaPlugin plugin;

    public MachineCraftListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();

        CustomMachine machine = MachineManager.getMachine(result.getType().name());
        if (machine == null) {
            return; // This is not a custom machine, so we don't need to intervene.
        }

        ItemStack[][] definedRecipe = machine.getRecipe();
        boolean matchesRecipe = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack craftingSlot = event.getInventory().getMatrix()[i * 3 + j];
                ItemStack definedSlot = definedRecipe[i][j];

                if ((craftingSlot == null && definedSlot != null) ||
                        (craftingSlot != null && !craftingSlot.getType().equals(definedSlot.getType()))) {
                    matchesRecipe = false;
                    break;
                }
            }
        }

        if (!matchesRecipe) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("Incorrect recipe for " + machine.getName() + "!");
        }
    }
}
