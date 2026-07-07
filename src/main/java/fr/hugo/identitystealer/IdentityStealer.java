package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enregistrement de la commande
        getCommand("steal").setExecutor(new StealCommand());
        
        // Enregistrement de l'écouteur d'événements (C'est ÇA qui manquait !)
        getServer().getPluginManager().registerEvents(new IdentityListener(), this);
        
        getLogger().info("IdentityStealer est maintenant totalement actif !");
    }
}
