package fr.hugo.identitystealer;

import org.bukkit.plugin.java.JavaPlugin;

public class IdentityStealer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enregistrement de la commande
        if (getCommand("steal") != null) {
            getCommand("steal").setExecutor(new StealCommand());
        }
        getLogger().info("IdentityStealer a bien démarré !");
    }
}
