package fr.hugo.identitystealer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class IdentityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cette méthode simple remplace le format du chat
        // On force l'affichage du nom comme tu le souhaites
        event.setFormat("%s: %s"); 
    }
}
