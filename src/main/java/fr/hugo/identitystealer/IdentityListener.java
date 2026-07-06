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

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;

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

        // Cas 1 : Le joueur ÉQUIPE une tête
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

                    // 2. Forçage via le plugin TAB (Nametag + Liste du TAB)
                    try {
                        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
                        if (tabPlayer != null) {
                            // On force la valeur personnalisée dans le plugin TAB
                            TabAPI.getInstance().getNameTagManager().setPrefix(tabPlayer, "");
                            TabAPI.getInstance().getNameTagManager().setSuffix(tabPlayer, "");
                            player.setPlayerListName(targetSkinName);
                            player.setDisplayName(targetSkinName);
                        }
                    } catch (Exception e) {
                        // Sécurité si l'API de TAB a un souci
                        player.setPlayerListName(targetSkinName);
                    }

                    player.sendMessage("§a🎭 Tu as volé l'identité de " + targetSkinName + " ! Enlève la tête pour redevenir toi-même.");
                }
            }
        } 
        // Cas 2 : Le joueur RETIRE la tête
        else {
            if (activeDisguises.containsKey(player.getUniqueId())) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinsrestorer clear " + player.getName());
                
                // Reset via le plugin TAB
                try {
                    TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
                    if (tabPlayer != null) {
                        TabAPI.getInstance().getNameTagManager().resetProperties(tabPlayer);
                    }
                } catch (Exception e) {
                    // Ignorer
                }

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
