package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enregistrement de tous nos détecteurs
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new SkinListener(this), this);
        getServer().getPluginManager().registerEvents(new IdentityListener(this), this);
        
        getLogger().info("==========================================");
        getLogger().info("IdentityStealer v1.0 par Hugo - ACTIVE");
        getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer v1.0 - DESACTIVE");
    }
}
