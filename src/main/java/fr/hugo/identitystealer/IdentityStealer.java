package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Uniquement notre listener principal actif
        getServer().getPluginManager().registerEvents(new IdentityListener(this), this);

        getLogger().info("=======================================");
        getLogger().info("IdentityStealer v1.0 - INITIALISE");
        getLogger().info("=======================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer v1.0 - DESACTIVE");
    }
}
