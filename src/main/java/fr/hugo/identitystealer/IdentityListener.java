package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import java.util.HashMap;
import java.util.UUID;

public class IdentityListener implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, String> activeDisguises = new HashMap<>();

    public IdentityListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        boolean isHelmetSlot = event.getSlot() == 39;
        boolean isCurrentItemHead = event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD;
        boolean isCursorItemHead = event.getCursor() != null && event.getCursor().getType() == Material.PLAYER_HEAD;

        if (isHelmetSlot || isCurrentItemHead || isCursorItemHead) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 1L);
        }
    }

    // Sécurité absolue : À chaque mouvement, si le joueur est déguisé, on force le masque invisible pour tout le monde
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            updateInvisibility(player);
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        // Cas 1 : Le joueur ÉQUIPE une tête
        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetSkinName = meta.getOwningPlayer().getName();
                if (targetSkinName != null) {
                    
                    if (targetSkinName.equals(activeDisguises.get(player.getUniqueId()))) {
                        updateInvisibility(player);
                        return;
                    }

                    activeDisguises.put(player.getUniqueId(), targetSkinName);
                    
                    // Applique le skin
                    player.performCommand("skin set " + targetSkinName);

                    // Changement d'identité (TAB, Chat, etc.)
                    player.customName(Component.text(targetSkinName));
                    player.setCustomNameVisible(true);
                    player.playerListName(Component.text(targetSkinName));
                    player.displayName(Component.text(targetSkinName));

                    updateInvisibility(player);
                    
                    player.sendMessage("§a🎭 Tu as volé l'identité de " + targetSkinName + " ! Enlève la tête de ton inventaire pour redevenir toi-même.");
                }
            }
        } 
        // Cas 2 : Le joueur RETIRE la tête
        else {
            if (activeDisguises.containsKey(player.getUniqueId())) {
                player.performCommand("skin clear");
                
                player.customName(null);
                player.setCustomNameVisible(false);
                player.playerListName(null);
                player.displayName(Component.text(player.getName()));
                
                activeDisguises.remove(player.getUniqueId());
                
                // On remet à jour l'affichage normal pour tout le monde
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, new ItemStack(Material.AIR));
                }
                player.sendMessage("§e🎭 Tu as repris ton identité d'origine.");
            }
        }
    }

    // Envoie en boucle le paquet "Pas de casque" pour contrer les mises à jour de Minecraft
    private void updateInvisibility(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, new ItemStack(Material.AIR));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            String fakeName = activeDisguises.get(player.getUniqueId());
            event.setFormat("<" + fakeName + "> %2$s");
        }
    }
}
