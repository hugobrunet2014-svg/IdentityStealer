package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collections;

public class PlayerKillListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // On vérifie que la victime a bien été tuée par un joueur
        if (killer != null) {
            // Création de l'item de type Tête de Joueur (PLAYER_HEAD)
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

            if (skullMeta != null) {
                // On attribue le skin de la victime à la tête
                skullMeta.setOwningPlayer(victim);
                
                // On lui donne un nom stylé en couleur avec Adventure (compatible 1.21.1)
                Component displayName = Component.text("Masque de ", NamedTextColor.RED)
                        .append(Component.text(victim.getName(), NamedTextColor.YELLOW))
                        .decoration(TextDecoration.ITALIC, false);
                skullMeta.displayName(displayName);
                
                // Petite description sur la tête (Lore)
                Component loreLine = Component.text("Équipez ce masque pour voler l'identité de ", NamedTextColor.GRAY)
                        .append(Component.text(victim.getName(), NamedTextColor.GOLD))
                        .decoration(TextDecoration.ITALIC, false);
                skullMeta.lore(Collections.singletonList(loreLine));
                
                playerHead.setItemMeta(skullMeta);
            }

            // On donne directement la tête au tueur (ou au sol si son inventaire est plein)
            if (killer.getInventory().firstEmpty() != -1) {
                killer.getInventory().addItem(playerHead);
                killer.sendMessage(Component.text("☠ Touer d'élite ! Tu as récupéré le visage de ", NamedTextColor.GREEN)
                        .append(Component.text(victim.getName(), NamedTextColor.YELLOW)));
            } else {
                victim.getLocation().getWorld().dropItemNaturally(victim.getLocation(), playerHead);
                killer.sendMessage(Component.text("⚠ Ton inventaire était plein, la tête de ", NamedTextColor.YELLOW)
                        .append(Component.text(victim.getName(), NamedTextColor.RED))
                        .append(Component.text(" est tombée au sol !", NamedTextColor.YELLOW)));
            }
        }
    }
}
