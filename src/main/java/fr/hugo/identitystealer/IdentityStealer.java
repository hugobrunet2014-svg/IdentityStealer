package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("IdentityStealer ON");
    }

    @Override
    public void onDisable() {
        getLogger().info("IdentityStealer OFF");
    }
}
