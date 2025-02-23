package org.jaibf.api;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jaibf.api.container.ReadonlyContainerPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InventoryManager {
    
    private static Validator schemaValidator;
    
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Map<String, ReadonlyContainerPreset> inventories;
    private final Map<UUID, InventoryController> controllers;

    private InventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = LoggerFactory.getLogger(plugin.getName() + "/InventoryManager");
        this.inventories = new HashMap<>();
        this.controllers = new HashMap<>();
    }
    
    public static InventoryManager forPlugin(JavaPlugin plugin) {
        InventoryManager inventoryManager = new InventoryManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(new PluginInventoryEvents(inventoryManager, plugin), plugin);
        return inventoryManager;
    }
    public void loadInventories(String... names) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            for (String name : names) {
                try (InputStream inputStream = plugin.getResource("inventories/" + name + ".inventory.xml")) {
                    Document document = documentBuilder.parse(inputStream);
                    if (schemaValidator != null) {
                        schemaValidator.validate(new DOMSource(document));
                    }
                    ReadonlyContainerPreset from = ReadonlyContainerPreset.from(document);
                    inventories.put(from.id(), from);
                } catch (IOException e) {
                    logger.error("Failed to load inventory {}", name, e);
                } catch (SAXException e) {
                    logger.error("Failed to validate inventory {}", name, e);
                }
            }
            logger.debug("Loaded {} inventories(for {})", inventories.size(), plugin.getName());
        } catch (ParserConfigurationException e) {
            logger.error("Failed to create document builder, no inventories are loaded", e);
        }
    }
    
    public static void setSchema(Schema schema) {
        InventoryManager.schemaValidator = schema.newValidator();
    }

    /**
     * Opens an inventory with given id for the player. The identifier
     * may not equal to the name you provided in {@link #loadInventories(String...)}.
     * The id is equal to the attribute "id" in the inventory XML file.
     * @param entity The entity to open the inventory for
     * @param id The id of the inventory to open
     */
    public void openInventory(HumanEntity entity, String id) {
        ReadonlyContainerPreset preset = inventories.get(id);
        if (preset == null) {
            logger.error("Inventory with id {} not found", id);
            return;
        }
        UUID instanceId = entity.getUniqueId();
        try {
            InventoryController controller = preset.controller().getConstructor().newInstance();
            controllers.put(instanceId, controller);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            plugin.getSLF4JLogger().error(
                    "Failed to instantiate controller class for inventory with id {}: {}", id, e.getMessage());
            plugin.getSLF4JLogger().warn("Make sure that the controller class has a public no-args constructor");
        }
    }
    
    Map<UUID, InventoryController> getActiveControllers() {
        return controllers;
    }
    
    InventoryController getControllerForPlayer(HumanEntity player) {
        return controllers.get(player.getUniqueId());
    }
}
