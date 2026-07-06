package fr.hugo.identitystealer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public class IdentityListener implements Listener {

    private final IdentityStealer plugin;
    private final HashMap<UUID, String> activeDisguises = new HashMap<>();

    public IdentityListener(IdentityStealer plugin) {
        this.plugin = plugin;
    }

    // NOUVEAU : Nettoyage quand le joueur part
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayerFromNameTag(event.getPlayer());
        activeDisguises.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(victim);
            head.setItemMeta(meta);
            event.getDrops().add(head);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 2L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkHelmet(player), 2L);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            String fakeName = activeDisguises.get(player.getUniqueId());
            event.setFormat(event.getFormat().replace(player.getName(), fakeName));
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        
        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetName = meta.getOwningPlayer().getName();
                
                // Si le joueur est déjà déguisé en ce nom, on ne fait rien pour économiser les ressources
                if (targetName.equals(activeDisguises.get(player.getUniqueId()))) return;

                activeDisguises.put(player.getUniqueId(), targetName);
                player.performCommand("skin set " + targetName);
                updateNameTag(player, targetName);
            }
        } else if (activeDisguises.containsKey(player.getUniqueId())) {
            player.performCommand("skin clear");
            resetNameTag(player);
            activeDisguises.remove(player.getUniqueId());
        }
    }

    private void updateNameTag(Player player, String fakeName) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("id_" + player.getName());
        if (team == null) team = board.registerNewTeam("id_" + player.getName());
        
        team.setPrefix(""); 
        team.addEntry(player.getName());
        
        player.setDisplayName(fakeName);
        player.setPlayerListName(fakeName);
        player.setCustomName(fakeName);
        player.setCustomNameVisible(true);
    }

    private void resetNameTag(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("id_" + player.getName());
        if (team != null) team.unregister();
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.setCustomName(null);
        player.setCustomNameVisible(false);
    }
}
