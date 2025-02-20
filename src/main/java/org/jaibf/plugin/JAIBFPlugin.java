package org.jaibf.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JAIBFPlugin extends JavaPlugin {
    private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private static JAIBFPlugin instance;
    private Schema schema;
    private Validator validator;
    private Map<String, List<Document>> inventories;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.inventories = new HashMap<>();
        try (InputStream schemaStream = getResource("jaibf-schema.xsd")) {
            schema = schemaFactory.newSchema(new StreamSource(schemaStream));
            validator = schema.newValidator();
        } catch (SAXException | IOException e) {
            getSLF4JLogger().error("Failed to load schema file, well, this should not happen?!", e);
        }
        
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void loadInventory(JavaPlugin plugin, String inventoryName) {
        getSLF4JLogger().debug("Initializing inventories for {}", plugin.getName());
        List<Document> documents = inventories.getOrDefault(plugin.getName(), new ArrayList<>());
        try (InputStream is = plugin.getResource("inventories/" + inventoryName + ".inventory.xml")) {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            getSLF4JLogger().debug("Validating inventory {}", inventoryName);
            validator.validate(new DOMSource(document));
            getSLF4JLogger().debug("Inventory {} is valid", inventoryName);
            documents.add(document);
        } catch (SAXException saxException) {
            getSLF4JLogger().error("Inventory {} is invalid", inventoryName, saxException);
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to load inventory {}", inventoryName, e);
        }
        inventories.put(plugin.getName(), documents);
        getSLF4JLogger().debug("Loaded {}, which now has {} entries.", inventoryName, documents.size());
    }
    public void loadInventories(JavaPlugin plugin, String... inventories) {
        getSLF4JLogger().debug("Loading mutltiple inventories for {}", plugin.getName());
        for(String inventory : inventories) {
            loadInventory(plugin, inventory);
        }
    }

    public Schema getSchema() {
        return schema;
    }
    
    public static JAIBFPlugin getInstance() {
        return instance;
    }
}
