package fr.hugo.identitystealer;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdentityListener implements Listener {

    // On stocke qui a volé l'identité de qui (UUID du voleur -> Nom de la victime)
    public static Map<UUID, String> stolenIdentities = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Si le joueur a volé une identité
        if (stolenIdentities.containsKey(player.getUniqueId())) {
            String victimName = stolenIdentities.get(player.getUniqueId());

            // On change le nom affiché dans le chat
            event.renderer((source, sourceDisplayName, message, viewer) -> 
                Component.text(victimName + ": ").append(message)
            );
        }
    }
}
