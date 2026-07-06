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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            String fakeName = activeDisguises.get(player.getUniqueId());
            String currentFormat = event.getFormat();
            event.setFormat(currentFormat.replace(player.getName(), fakeName));
            player.setDisplayName(fakeName);
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                String targetName = meta.getOwningPlayer().getName();
                if (targetName != null && !targetName.equals(activeDisguises.get(player.getUniqueId()))) {
                    activeDisguises.put(player.getUniqueId(), targetName);
                    player.performCommand("skin set " + targetName);
                    player.setDisplayName(targetName);
                    player.setPlayerListName(targetName);
                    updatePlayerNameTag(player, targetName);
                    player.sendMessage("§a🎭 Identité volée : " + targetName);
                }
            }
        } else if (activeDisguises.containsKey(player.getUniqueId())) {
            player.performCommand("skin clear");
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            removePlayerFromNameTag(player);
            activeDisguises.remove(player.getUniqueId());
            player.sendMessage("§e🎭 Identité rendue.");
        }
    }

    private void updatePlayerNameTag(Player player, String fakeName) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("id_" + player.getName());
        if (team == null) team = board.registerNewTeam("id_" + player.getName());
        team.setPrefix(fakeName + " ");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.HIDE_FOR_OTHER_TEAMS);
        team.addEntry(player.getName());
        player.setCustomName(fakeName);
        player.setCustomNameVisible(true);
    }

    private void removePlayerFromNameTag(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("id_" + player.getName());
        if (team != null) team.unregister();
        player.setCustomName(null);
        player.setCustomNameVisible(false);
    }
}
