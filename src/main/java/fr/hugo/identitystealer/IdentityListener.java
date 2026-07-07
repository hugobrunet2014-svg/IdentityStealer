package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class IdentityListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.PLAYER_HEAD) {
            // Exemple : si besoin d'une action spéciale au clic, on l'ajoutera ici
        }
    }
}
