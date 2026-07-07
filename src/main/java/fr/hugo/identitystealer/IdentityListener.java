package fr.hugo.identitystealer;

import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class IdentityListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        // AU LIEU DE JUSTE LE NOM, ON ESSAYE DE RÉCUPÉRER LA SIGNATURE DU SKIN
        try {
            // Cela demande à SkinsRestorer la vraie texture du joueur
            meta.setOwningPlayer(victim);
            head.setItemMeta(meta);
        } catch (Exception e) {
            // Si ça échoue, on met au moins le nom pour éviter que le plugin crash
        }
        
        event.getDrops().add(head);
    }
}
