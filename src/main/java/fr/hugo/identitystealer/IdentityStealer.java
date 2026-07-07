package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enregistrement des commandes
        getCommand("steal").setExecutor(new StealCommand());
        
        // Enregistrement des listeners
        getServer().getPluginManager().registerEvents(new IdentityListener(), this);
        
        getLogger().info("IdentityStealer est activé avec succès !");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer est désactivé.");
    }
}
