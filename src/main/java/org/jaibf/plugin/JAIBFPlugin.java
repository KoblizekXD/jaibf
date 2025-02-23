package org.jaibf.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jaibf.api.InventoryManager;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

public final class JAIBFPlugin extends JavaPlugin implements CommandExecutor {
    private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private InventoryManager inventoryManager;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try (InputStream schemaStream = getResource("jaibf-schema.xsd")) {
            InventoryManager.setSchema(schemaFactory.newSchema(new StreamSource(schemaStream)));
        } catch (SAXException | IOException e) {
            getSLF4JLogger().error("Failed to load schema file, well, this should not happen?!", e);
        }
        
        inventoryManager = InventoryManager.forPlugin(this);
        inventoryManager.loadInventories("test");
        getCommand("testinventory").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        inventoryManager.openInventory(((Player) sender), "test");
        return true;
    }
}
