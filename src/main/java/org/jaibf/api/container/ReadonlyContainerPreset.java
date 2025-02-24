package org.jaibf.api.container;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jaibf.api.InventoryController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record ReadonlyContainerPreset(String id, String title, Class<InventoryController> controller,
                                      List<Page> pages, int height, String onOpen, String onClose) {

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
        
        public ItemStack getItemAt(int x, int y) {
            return Arrays.stream(items).filter(item -> item.x() == x && item.y() == y)
                    .findFirst()
                    .map(PageItem::itemStack)
                    .orElse(null);
        }
        
        public PageItem getPageItemAt(int x, int y) {
            return Arrays.stream(items).filter(item -> item.x() == x && item.y() == y).findFirst().orElse(null);
        }
        
        public ItemStack getItemById(String id) {
            return Arrays.stream(items).filter(item -> item.id().equals(id)).findFirst()
                    .map(PageItem::itemStack)
                    .orElse(null);
        }
    }

    public record PageItem(String id, int x, int y, String onClick, ItemStack itemStack) {
        @Override
        public String toString() {
            return "PageItem(id=" + id + ", item=" + itemStack.getType() + ")";
        }
    }
    
    public Page findPageById(String id) {
        if (id == null) return pages.getFirst();
        return pages.stream().filter(page -> page.id().equals(id)).findFirst().orElse(null);
    }
    
    public static ReadonlyContainerPreset from(Document document) {
        Class<InventoryController> controllerClass;

        try {
            controllerClass = (Class<InventoryController>) Class.forName(document.getDocumentElement().getAttribute("class"));
        } catch (ClassNotFoundException e) {
            controllerClass = null;
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid controller class(must extend org.jaibf.api.InventoryController)", e);
        }

        List<Page> pageList = new ArrayList<>();
        NodeList pageNodes = document.getElementsByTagName("Page");

        for (int i =  0; i < pageNodes.getLength(); i++) {
            Element pageElement = (Element) pageNodes.item(i);
            String pageId = pageElement.getAttribute("id");

            List<PageItem> itemList = new ArrayList<>();
            NodeList itemNodes = pageElement.getElementsByTagName("Item");
            for (int j = 0; j < itemNodes.getLength(); j++) {
                Element itemElement = (Element) itemNodes.item(j);
                String itemId = itemElement.getAttribute("id");
                int x = Integer.parseInt(itemElement.getAttribute("x"));
                int y = Integer.parseInt(itemElement.getAttribute("y"));
                String onClick = itemElement.getAttribute("onClick");
                Material material = Material.matchMaterial(itemElement.getAttribute("material"));
                if(material == null ){
                    throw new RuntimeException("Invalid material" + itemElement.getAttribute("material"));
                }
                ItemStack itemStack = new ItemStack(material);

                itemList.add(new PageItem(itemId, x, y, onClick, itemStack));
            }
            Page page = new Page(pageId, itemList.toArray(new PageItem[0]));
            pageList.add(page);
        }
        return new ReadonlyContainerPreset(
                document.getDocumentElement().getAttribute("id"),
                document.getDocumentElement().getAttribute("title"),
                controllerClass,
                pageList,
                Integer.parseInt(document.getDocumentElement().getAttribute("height")),
                document.getDocumentElement().getAttribute("onOpen"),
                document.getDocumentElement().getAttribute("onClose"));
    }
}
