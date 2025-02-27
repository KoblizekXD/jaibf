package org.jaibf.plugin;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jaibf.api.InventoryController;

public class TestInventory extends InventoryController {
    public void onOpen(InventoryOpenEvent event) {
        System.out.println("Inventory Opened");
    }
    public void onClose(InventoryCloseEvent event) {System.out.println("Inventory Closed");}
}
