package org.jaibf.api;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

class PluginInventoryEvents implements Listener {
    private final InventoryManager inventoryManager;
    private final JavaPlugin plugin;

    public PluginInventoryEvents(InventoryManager inventoryManager, JavaPlugin plugin) {
        this.inventoryManager = inventoryManager;
        this.plugin = plugin;
    }
}
