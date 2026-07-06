package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;

public class IdentityListener implements Listener {

    private final JavaPlugin plugin;

    public IdentityListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTaskLater(plugin, () -> updateIdentity(player), 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updateIdentity(player), 1L);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String fakeName = meta.getOwningPlayer().getName();
                if (fakeName != null) {
                    // Modifie le format du chat pour afficher le faux nom
                    event.setFormat("<" + fakeName + "> %2$s");
                }
            }
        }
    }

    private void updateIdentity(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String fakeName = meta.getOwningPlayer().getName();
                if (fakeName != null) {
                    player.customName(Component.text(fakeName));
                    player.setCustomNameVisible(true);
                    player.playerListName(Component.text(fakeName));
                    player.displayName(Component.text(fakeName));
                    return;
                }
            }
        }
        
        // Reset complet quand on l'enlève
        player.customName(null);
        player.setCustomNameVisible(false);
        player.playerListName(null);
        player.displayName(Component.text(player.getName()));
    }
}
