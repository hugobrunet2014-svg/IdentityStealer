package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    private static IdentityStealer instance;

    @Override
    public void onEnable() {
        instance = this;
        // Enregistrement des événements
        getServer().getPluginManager().registerEvents(new IdentityListener(), this);
        // Enregistrement de la commande
        getCommand("steal").setExecutor(new StealCommand());
        getLogger().info("IdentityStealer est activé !");
    }

    public static IdentityStealer getInstance() {
        return instance;
    }
}
