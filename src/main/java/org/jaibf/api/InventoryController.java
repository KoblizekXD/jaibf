package org.jaibf.api;

import org.jaibf.api.container.ReadonlyContainerPreset;

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
 *     }
 * </p>
 */
public abstract class InventoryController {
    
    private ReadonlyContainerPreset containerPreset;
    private String pageId;
    
    /**
     * Sets the page of the inventory with the given id and updates all members of the controller.
     * @param id The id of the page to set
     * @return {@code true} if the page was set successfully, {@code false} otherwise
     */
    public boolean setPage(String id) {
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
    public String getPage() {
        return pageId;
    }
}
