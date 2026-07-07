package fr.hugo.identitystealer;

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
        
        // Créer l'item Tête
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        // Assigner le propriétaire de la tête
        meta.setOwningPlayer(victim);
        head.setItemMeta(meta);
        
        // Ajouter la tête au loot de la mort
        event.getDrops().add(head);
    }
}
