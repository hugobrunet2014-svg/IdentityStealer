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
        }
    }

    private void checkHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) helmet.getItemMeta();
            if (meta != null) {
                
                String targetSkinName = null;
                if (meta.getOwningPlayer() != null) {
                    targetSkinName = meta.getOwningPlayer().getName();
                }

                if (targetSkinName != null && !targetSkinName.isEmpty()) {
                    
                    if (targetSkinName.equals(activeDisguises.get(player.getUniqueId()))) {
                        return;
                    }

                    activeDisguises.put(player.getUniqueId(), targetSkinName);
                    
                    // Applique le skin
                    player.performCommand("skin set " + targetSkinName);
                    
                    // Modifie le nom dans le chat et la liste TAB
                    player.setDisplayName(targetSkinName);
                    player.setPlayerListName(targetSkinName);
                    
                    // MODIFIE LE NOM AU-DESSUS DE LA TÊTE (Système d'Équipe)
                    updatePlayerNameTag(player, targetSkinName);
                    
                    player.sendMessage("§a🎭 Tu as volé l'identité de " + targetSkinName + " ! Enlève la tête pour reprendre la tienne.");
                }
            }
        } else {
            if (activeDisguises.containsKey(player.getUniqueId())) {
                
                player.performCommand("skin clear");
                
                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getName());
                
                // RETIRE LE JOUEUR DE L'ÉQUIPE DU FAUX PSEUDO
                removePlayerFromNameTag(player);
                
                activeDisguises.remove(player.getUniqueId());
                
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, player.getInventory().getHelmet() != null ? player.getInventory().getHelmet() : new ItemStack(Material.AIR));
                }
                
                player.sendMessage("§e🎭 Tu as repris ton identité d'origine.");
            }
        }
    }

    private void updatePlayerNameTag(Player player, String fakeName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "ids_" + player.getName();
        
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        // On change le préfixe ou le nom affiché dans l'équipe pour correspondre au faux pseudo
        team.setPrefix(fakeName + " ");
        // Note : Pour changer radicalement le texte brut au-dessus sans pack de textures ou gros plugins,
        // l'utilisation du préfixe combinée à la disparition du pseudo d'origine est requise, ou bien on le gère ici :
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    private void removePlayerFromNameTag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "ids_" + player.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }
}
