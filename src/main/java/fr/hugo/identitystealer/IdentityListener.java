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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (activeDisguises.containsKey(player.getUniqueId())) {
            updateInvisibility(player);
            
            String fakeName = activeDisguises.get(player.getUniqueId());
            player.sendActionBar(Component.text("§eIdentité actuelle : §6" + fakeName + " §a🎭"));
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

                    // --- FORCE LE RECOUVREMENT DU PSEUDO AU-DESSUS DE LA TÊTE (NAMETAG) ---
                    setupFakeNametag(player, targetSkinName);

                    // Changement dans le TAB et nom d'affichage interne
                    player.playerListName(Component.text(targetSkinName));
                    player.displayName(Component.text(targetSkinName));

                    updateInvisibility(player);
                    
                    player.showTitle(net.kyori.adventure.title.Title.title(
                        Component.text("§aIdentité Volée !"),
                        Component.text("§7Tu es maintenant §e" + targetSkinName)
                    ));
                }
            }
        } 
        // Cas 2 : Le joueur RETIRE la tête
        else {
            if (activeDisguises.containsKey(player.getUniqueId())) {
                player.performCommand("skin clear");
                
                // --- SUPPRIME LE FAUX PSEUDO AU-DESSUS DE LA TÊTE ---
                removeFakeNametag(player);
                
                player.playerListName(Component.text(player.getName()));
                player.displayName(Component.text(player.getName()));
                
                activeDisguises.remove(player.getUniqueId());
                
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, new ItemStack(Material.AIR));
                }
                
                player.sendActionBar(Component.text(""));
                player.sendMessage("§e🎭 Tu as repris ton identité d'origine.");
            }
        }
    }

    // Crée une équipe dédiée pour modifier le nom affiché au-dessus du joueur
    private void setupFakeNametag(Player player, String fakeName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "id_" + player.getUniqueId().toString().substring(0, 10);
        
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        // On remplace le préfixe ou le nom par le faux pseudo
        team.prefix(Component.text(""));
        team.suffix(Component.text(""));
        player.customName(Component.text(fakeName));
        player.setCustomNameVisible(true);
        
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    // Supprime l'équipe et restaure le nametag par défaut
    private void removeFakeNametag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "id_" + player.getUniqueId().toString().substring(0, 10);
        
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.unregister();
        }
        player.customName(null);
        player.setCustomNameVisible(false);
    }

    private void updateInvisibility(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, new ItemStack(Material.AIR));
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
