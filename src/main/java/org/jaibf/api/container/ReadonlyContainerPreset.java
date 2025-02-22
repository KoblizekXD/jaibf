package org.jaibf.api.container;

import org.bukkit.inventory.ItemStack;
import org.jaibf.api.InventoryController;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record ReadonlyContainerPreset(String onOpen, String id, String title, Class<InventoryController> controller,
                                      List<Page> pages, int height, String onClose) {

    public record Page(String id, PageItem... items) {
        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Page page = (Page) obj;
            return id.equals(page.id);
        }

        @Override
        public String toString() {
            return "Page(id=" + id + ")" + Arrays.stream(items).map(PageItem::toString).collect(Collectors.joining(", ", "[", "]"));
        }
    }

    public record PageItem(String id, int x, int y, String onClick, ItemStack itemStack) {
        @Override
        public String toString() {
            return "PageItem(id=" + id + ", item=" + itemStack.getType() + ")";
        }
    }
    
    public static ReadonlyContainerPreset from(Document document) {
        
    }
}
