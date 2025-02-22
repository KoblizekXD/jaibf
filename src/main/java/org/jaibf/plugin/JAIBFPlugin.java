package org.jaibf.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jaibf.api.InventoryManager;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

public final class JAIBFPlugin extends JavaPlugin {
    private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try (InputStream schemaStream = getResource("jaibf-schema.xsd")) {
            InventoryManager.setSchema(schemaFactory.newSchema(new StreamSource(schemaStream)));
        } catch (SAXException | IOException e) {
            getSLF4JLogger().error("Failed to load schema file, well, this should not happen?!", e);
        }
    }
}
