package fr.hugo.identitystealer;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.Optional;

public class SkinListener implements Listener {

    private final JavaPlugin plugin;
    private final SkinsRestorer skinsRestorer;

    public SkinListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.skinsRestorer = SkinsRestorerProvider.get();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor(player), 1L);
    }

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

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetSkinName = meta.getOwningPlayer().getName();
                if (targetSkinName != null) {
                    try {
                        // Méthode sûre : On récupère le skin par le nom via le PropertyUtils
                        Optional<SkinProperty> skin = skinsRestorer.getPropertyUtils().getSkinProperty(targetSkinName);
                        if (skin.isPresent()) {
                            skinsRestorer.getPlayerStorage().setSkinIdOfPlayer(player.getUniqueId(), skin.get().getSkinId());
                            skinsRestorer.getSkinApplier(Player.class).applySkin(player);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Impossible d'appliquer le skin pour " + player.getName());
                    }
                }
            }
        } else {
            try {
                // Méthode sûre pour reset le skin du joueur
                skinsRestorer.getPlayerStorage().removeSkinIdOfPlayer(player.getUniqueId());
                skinsRestorer.getSkinApplier(Player.class).applySkin(player);
            } catch (Exception e) {
                // Rien à reset
            }
        }
    }
}
