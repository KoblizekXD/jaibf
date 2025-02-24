package org.jaibf.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jaibf.api.container.ReadonlyContainerPreset;

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
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryController controller = inventoryManager.getControllerForPlayer(event.getWhoClicked());
        if (controller == null) return;
        int y = event.getSlot() / 9;
        int x = event.getSlot() % 9;
        ReadonlyContainerPreset.PageItem pageItem = controller.getContainerPreset()
                .findPageById(controller.getPage())
                .getPageItemAt(x, y);
        if (pageItem.onClick() != null) {
            try {
                Method method = controller.getClass().getMethod(pageItem.onClick(), InventoryClickEvent.class);
                method.invoke(controller, event);
            } catch (NoSuchMethodException e) {
                plugin.getSLF4JLogger().warn("Method {}(for event onClick) was not found in controller {}", pageItem.onClick(), 
                        controller.getClass().getName());
            } catch (InvocationTargetException | IllegalAccessException e) {
                plugin.getSLF4JLogger().error("Failed to invoke method {}(for event onClick) in controller {}: {}", pageItem.onClick(), 
                        controller.getClass().getName(), e.getMessage());
            } 
        }
    }
}
