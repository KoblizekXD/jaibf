package org.jaibf.api;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jaibf.api.container.ReadonlyContainerPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Base class for handling all inventory events. It is bound to each of inventory's
 * schemas and can be set using the {@code <Inventory class="" />} attribute.
 * 
 * <p>
 *     Here's an example of a simple inventory controller:
 *     {@snippet lang="java":
 *     public class MyInventoryController extends InventoryController {
 *         @Slot("my_slot")
 *         private ItemStack mySlot;
 *     
 *         @Slot(value = "my_other_slot", page = "second_page")
 *         private ItemStack myOtherSlot;
 *     }
 *     }e
 *
 * </p>
 */
public abstract class InventoryController {
    public static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private ReadonlyContainerPreset containerPreset;
    private Inventory bukkitInventory;
    private String pageId;
    
    /**
     * Sets the page of the inventory with the given id and updates all members of the controller.
     * @param id The id of the page to set
     * @return {@code true} if the page was set successfully, {@code false} otherwise
     */
    public final boolean setPage(String id) {
        if (containerPreset.pages().stream().anyMatch(page -> page.id().equals(id))) {
            pageId = id;
            return true;
        }
        return false;
    }

    /**
     * Gets the current page id of the inventory.
     * @return The current page id of the inventory
     */
    public final String getPage() {
        return pageId;
    }

    public final void setContainerPreset(ReadonlyContainerPreset containerPreset) {
        if (this.containerPreset != null) {
            throw new IllegalStateException("Container preset can be only set during initialization");
        }
        this.containerPreset = containerPreset;
    }
    
    private void reloadPage() {
        if (pageId == null) {
            pageId = containerPreset.pages().get(0).id();
        }
        ReadonlyContainerPreset.Page page = containerPreset.pages().stream()
                .filter(p -> p.id().equals(pageId))
                .findFirst().get();
        for (ReadonlyContainerPreset.PageItem item : page.items()) {
            int slotIndex = item.y() * 9 + item.x();
            bukkitInventory.clear();
            bukkitInventory.setItem(slotIndex, item.itemStack());
        }

        for (Field field : getClass().getFields()) {
            if (field.isAnnotationPresent(Slot.class)) {
                Slot slot = field.getAnnotation(Slot.class);
                try {
                    if (slot.page().isEmpty()) {
                        if (field.getType() == Material.class)
                            field.set(this, page.getItemById(slot.value()).getType());
                        else if (field.getType() == ItemStack.class)
                            field.set(this, page.getItemById(slot.value()));
                    } else {
                        if (slot.page().equals(pageId)) {
                            if (field.getType() == Material.class)
                                field.set(this, page.getItemById(slot.value()).getType());
                            else if (field.getType() == ItemStack.class)
                                field.set(this, page.getItemById(slot.value()));
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Failed to set field {} in controller {}: {}", field.getName(), getClass().getName(), e.getMessage());
                }
            }
        }
    }

    public final Inventory getBukkitInventory() {
        return bukkitInventory;
    }

    final ReadonlyContainerPreset getContainerPreset() {
        return containerPreset;
    }
}
