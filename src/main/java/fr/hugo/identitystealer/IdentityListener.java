package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import io.papermc.paper.chat.ChatRenderer;

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

    // Force le chat à afficher le faux nom si le joueur porte un masque
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String fakeName = meta.getOwningPlayer().getName();
                if (fakeName != null) {
                    // On modifie l'affichage du nom dans le chat de manière moderne (Paper 1.21+)
                    event.renderer((source, sourceDisplayName, message, viewer) -> 
                        Component.text("<" + fakeName + "> ").append(message)
                    );
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
                    // Change le nom au-dessus de la tête et dans le TAB
                    player.customName(Component.text(fakeName));
                    player.setCustomNameVisible(true);
                    player.playerListName(Component.text(fakeName));
                    player.displayName(Component.text(fakeName));
                    return;
                }
            }
        }
        
        // Reset si on enlève le masque
        player.customName(null);
        player.setCustomNameVisible(false);
        player.playerListName(null);
        player.displayName(Component.text(player.getName()));
    }
}
