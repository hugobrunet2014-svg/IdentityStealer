package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new IdentityListener(this), this);

        getLogger().info("=======================================");
        getLogger().info("IdentityStealer v1.0 - CODES ORIGINAUX REPRIS");
        getLogger().info("=======================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer v1.0 - DESACTIVE");
    }
}
