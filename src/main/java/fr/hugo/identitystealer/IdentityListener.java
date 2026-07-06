package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
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

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetSkinName = meta.getOwningPlayer().getName();
                if (targetSkinName != null) {
                    
                    activeDisguises.put(player.getUniqueId(), targetSkinName);
                    
                    // 1. La console applique le skin
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinsrestorer set " + player.getName() + " " + targetSkinName);

                    // 2. Changement d'identité complet (TAB, Chat, etc.)
                    player.customName(Component.text(targetSkinName));
                    player.setCustomNameVisible(true);
                    player.playerListName(Component.text(targetSkinName));
                    player.displayName(Component.text(targetSkinName));

                    // 3. On attend 5 ticks (un quart de seconde) pour laisser le skin s'afficher,
                    // puis on retire la grosse tête de l'armure en toute sécurité !
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.getInventory().setHelmet(null);
                    }, 5L);

                    player.sendMessage("§a🎭 Tu as volé l'identité de " + targetSkinName + " ! Tape /unmask pour reprendre ton apparence.");
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/unmask")) {
            event.setCancelled(true);
            if (activeDisguises.containsKey(player.getUniqueId())) {
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinsrestorer clear " + player.getName());
                
                player.customName(null);
                player.setCustomNameVisible(false);
                player.playerListName(null);
                player.displayName(Component.text(player.getName()));
                
                activeDisguises.remove(player.getUniqueId());
                player.sendMessage("§e🎭 Tu as repris ton identité d'origine.");
            } else {
                player.sendMessage("§cTu n'es pas déguisé actuellement.");
            }
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
