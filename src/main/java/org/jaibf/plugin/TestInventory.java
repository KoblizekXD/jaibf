package org.jaibf.plugin;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jaibf.api.InventoryController;

public class TestInventory extends InventoryController {
    public void onOpen(InventoryOpenEvent event) {
        System.out.println("hello!");
    }
}
