package com.massivecraft.factions.integration;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Conf;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;

public class EssentialsIntegration {

    private static Essentials essentials;

    public static void setup() {
        essentials = JavaPlugin.getPlugin(Essentials.class);
    }

    // return false if feature is disabled or Essentials isn't available
    @SuppressWarnings("deprecation")
	public static boolean handleTeleport(Player player, Location loc) {
        if (!Conf.homesTeleportCommandEssentialsIntegration || essentials == null) return false;

        Teleport teleport = essentials.getUser(player).getTeleport();
        Trade trade = new Trade(BigDecimal.valueOf(Conf.econCostHome), essentials);
        try {
            teleport.teleport(loc, trade, TeleportCause.PLUGIN);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }

    public static boolean isVanished(Player player) {
        // Edge case handling.
        if (player == null) return false;
        boolean vanish = false;
        if (essentials != null) {
            User user = essentials.getUser(player);
            if (user != null && user.isVanished()) return true;
        }
        if (player.hasMetadata("vanished"))
            for (MetadataValue meta : player.getMetadata("vanished")) {
                if (meta == null) continue;
                if (meta.asBoolean()) {
                    vanish = true;
                    break;
                }
            }
        return vanish;
    }
}
