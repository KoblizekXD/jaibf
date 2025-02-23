package org.jaibf.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryController controller = inventoryManager.getControllerForPlayer(event.getPlayer());
        if (controller == null) return;
        String openMethod = controller.getContainerPreset().onOpen();
        if (openMethod == null) return;
        try {
            Method method = controller.getClass().getMethod(openMethod, InventoryOpenEvent.class);
            method.invoke(controller, event);
        } catch (NoSuchMethodException e) {
            plugin.getSLF4JLogger().warn("Method {}(for event onOpen) was not found in controller {}", openMethod, 
                    controller.getClass().getName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            plugin.getSLF4JLogger().error("Failed to invoke method {}(for event onOpen) in controller {}: {}", openMethod, 
                    controller.getClass().getName(), e.getMessage());
        } 
    }
}
