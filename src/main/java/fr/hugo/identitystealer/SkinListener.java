package fr.hugo.identitystealer;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEven;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class SkinListener implements Listener {

    private final JavaPlugin plugin;
    private final SkinsRestorer skinsRestorer;

    public SkinListener(JavaPlugin plugin) {
        this.plugin = plugin;
        // On se connecte à l'API de SkinsRestorer
        this.skinsRestorer = SkinsRestorerProvider.getApi();
    }

    // Détecte quand on clique dans l'inventaire pour mettre/enlever le casque
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (event.getWhoClicked() instanceof Player) ? (Player) event.getWhoClicked() : null;
        if (player == null) return;

        // On attend la fin du clic pour checker l'armure
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor(player), 1L);
    }

    // Détecte quand on fait un clic droit avec la tête en main pour l'équiper
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor(player), 1L);
        }
    }

    private void checkArmor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        PlayerStorage playerStorage = skinsRestorer.getPlayerStorage();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetSkinName = meta.getOwningPlayer().getName();
                
                if (targetSkinName != null) {
                    try {
                        // On applique le skin de la victime via SkinsRestorer
                        playerStorage.setSkinOfPlayer(player.getUniqueId(), targetSkinName);
                        // On force la mise à jour visuelle pour tout le monde
                        skinsRestorer.getSkinApplier(Player.class).applySkin(player);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Impossible d'appliquer le skin pour " + player.getName());
                    }
                }
            }
        } else {
            // Si le joueur n'a plus de tête de joueur sur lui, on remet son skin d'origine
            try {
                playerStorage.removeSkinOfPlayer(player.getUniqueId());
                skinsRestorer.getSkinApplier(Player.class).applySkin(player);
            } catch (Exception e) {
                // Pas de skin personnalisé actif, rien à enlever
            }
        }
    }
}
