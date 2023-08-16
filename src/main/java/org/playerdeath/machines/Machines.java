package org.playerdeath.machines;

import org.bukkit.plugin.java.JavaPlugin;

public final class Machines extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save the default configuration if it doesn't exist
        saveDefaultConfig();

        // Initialize MachineManager and load machines from the config
        MachineManager.loadMachines(this);

        // Set command executor for the /machinegui command
        this.getCommand("machinegui").setExecutor(new MachineGUICommand(this));

        // Register the MachineCraftListener
        getServer().getPluginManager().registerEvents(new MachineCraftListener(this), this);
    }

    @Override
    public void onDisable() {
        // You can save configurations or handle other shutdown logic here if needed
    }
}
