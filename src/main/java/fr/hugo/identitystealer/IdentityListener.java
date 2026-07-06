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
        if (event.getSlot() == 39) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.PLAYER_HEAD) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 1L);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            String fakeName = activeDisguises.get(player.getUniqueId());
            event.setFormat(event.getFormat().replace(player.getName(), fakeName));
            player.setDisplayName(fakeName);
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetName = meta.getOwningPlayer().getName();
                if (!targetName.equals(activeDisguises.get(player.getUniqueId()))) {
                    activeDisguises.put(player.getUniqueId(), targetName);
                    player.performCommand("skin set " + targetName);
                    player.setDisplayName(targetName);
                    player.setPlayerListName(targetName);
                    player.sendMessage("§a🎭 Identité volée : " + targetName);
                }
            }
        } else if (activeDisguises.containsKey(player.getUniqueId())) {
            player.performCommand("skin clear");
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            activeDisguises.remove(player.getUniqueId());
            player.sendMessage("§e🎭 Identité rendue.");
        }
    }
}
