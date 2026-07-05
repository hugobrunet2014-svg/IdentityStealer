package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enregistrement du Listener de Kill
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        
        getLogger().info("==========================================");
        getLogger().info("IdentityStealer v1.0 par Hugo - ACTIVE");
        getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer v1.0 - DESACTIVE");
    }
}
