package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class IdentityListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Logique : si le joueur fait un clic droit avec un objet spécifique
        if (event.getItem() != null && event.getItem().getType() == Material.PLAYER_HEAD) {
            // Ici, tu pourras appeler ta méthode de vol
        }
    }
}
