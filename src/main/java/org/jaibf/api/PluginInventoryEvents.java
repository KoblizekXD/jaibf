package org.jaibf.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

class PluginInventoryEvents implements Listener {
    private final InventoryManager inventoryManager;
    private final JavaPlugin plugin;

    public PluginInventoryEvents(InventoryManager inventoryManager, JavaPlugin plugin) {
        this.inventoryManager = inventoryManager;
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        InventoryController inventoryController = inventoryManager.getActiveControllers().get(event.getPlayer().getUniqueId());
        if (inventoryController == null) return;
        inventoryManager.getActiveControllers().remove(event.getPlayer().getUniqueId());
    }
}
