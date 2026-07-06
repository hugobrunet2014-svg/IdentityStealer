package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, new ItemStack(Material.AIR));
            }
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetSkinName = meta.getOwningPlayer().getName();
                if (targetSkinName != null) {
                    
                    if (targetSkinName.equals(activeDisguises.get(player.getUniqueId()))) {
                        return;
                    }

                    activeDisguises.put(player.getUniqueId(), targetSkinName);
                    
                    // 1. Skin
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinsrestorer set " + player.getName() + " " + targetSkinName);

                    // 2. Commande forcée pour le plugin TAB
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " customtabname " + targetSkinName);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " customtagname " + targetSkinName);

                    player.setPlayerListName(targetSkinName);
                    player.setDisplayName(targetSkinName);
                    
                    player.sendMessage("§a🎭 Tu as volé l'identité de " + targetSkinName + " ! Enlève la tête pour redevenir toi-même.");
                }
            }
        } else {
            if (activeDisguises.containsKey(player.getUniqueId())) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinsrestorer clear " + player.getName());
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " remove customtabname");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + player.getName() + " remove customtagname");

                player.setPlayerListName(player.getName());
                player.setDisplayName(player.getName());
                
                activeDisguises.remove(player.getUniqueId());
                
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, player.getInventory().getHelmet() != null ? player.getInventory().getHelmet() : new ItemStack(Material.AIR));
                }
                
                player.sendMessage("§e🎭 Tu as repris ton identité d'origine.");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            String fakeName = activeDisguises.get(player.getUniqueId());
            event.setFormat("<" + fakeName + "> %2$s");
        }
    }
}
