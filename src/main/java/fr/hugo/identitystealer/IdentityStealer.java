package fr.hugo.identitystealer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {
    
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        // Enregistrement de la commande
        getCommand("steal").setExecutor(new StealCommand());
        getLogger().info("IdentityStealer activé avec ProtocolLib !");
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
