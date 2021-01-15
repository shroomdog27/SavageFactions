package com.massivecraft.factions.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.cmd.CmdSeeChunk;
import com.massivecraft.factions.event.FPlayerEnteredFactionEvent;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.scoreboards.sidebar.FDefaultSidebar;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class FactionsPlayerListener implements Listener {

    // Map of saving falling players from fall damage after F-Fly leaves.
    HashMap<Player, Boolean> fallMap = new HashMap<>();

    // Holds the next time a player can have a map shown.
    private HashMap<UUID, Long> showTimes = new HashMap<>();

    public FactionsPlayerListener() {
        for (Player player : SavageFactions.plugin.getServer().getOnlinePlayers())
            initPlayer(player);
        if (positionTask == null)
            startPositionCheck();
    }

    public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck,
            PermissableAction permissableAction) {
        if (Conf.playersWhoBypassAllProtection.contains(player.getName()))
            return true;

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing())
            return true;

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);

        // We handle ownership protection below.

        if (me.getFaction() == otherFaction)
            return true;

        if (SavageFactions.plugin.getConfig().getBoolean("hcf.raidable", false)
                && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
            return true;

        if (otherFaction.isWilderness()) {
            if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
                return true;
            if (!justCheck)
                me.msg(TL.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
            return false;
        } else if (otherFaction.isSafeZone()) {
            if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player))
                return true;
            if (!justCheck)
                me.msg(TL.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
            return false;
        } else if (otherFaction.isWarZone()) {
            if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player))
                return true;
            if (!justCheck)
                me.msg(TL.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
            return false;
        }

        // We should only after knowing it's not wilderness, otherwise gets bypassed
        if (otherFaction.hasPlayersOnline()) {
            // This should be inverted to prevent bypasing
            if (Conf.territoryDenyUseageMaterials.contains(material))
                return false; // Item should not be used, deny.
        } else {
            if (Conf.territoryDenyUseageMaterialsWhenOffline.contains(material))
                return false; // Item should not be used, deny.
        }

        Access access = otherFaction.getAccess(me, permissableAction);
        return CheckPlayerAccess(player, me, loc, otherFaction, access, permissableAction, false);
    }

    public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
        if (Conf.playersWhoBypassAllProtection.contains(player.getName()))
            return true;

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing())
            return true;

        // Dupe fix.
        FLocation loc = new FLocation(block);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();

        // no door/chest/whatever protection in wilderness, war zones, or safe zones
        if (otherFaction.isSystemFaction())
            return true;

        if (myFaction.isWilderness()) {
            me.msg(TL.GENERTIC_ACTION_NOPERMISSION, block.getType().toString().replace("_", " "));
            return false;
        }

        if (SavageFactions.plugin.getConfig().getBoolean("hcf.raidable", false)
                && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
            return true;

        if (otherFaction.getId().equals(myFaction.getId()) && me.getRole() == Role.LEADER)
            return true;
        PermissableAction action = GetPermissionFromUsableBlock(block);
        if (action == null)
            return false;
        // Move up access check to check for exceptions
        if (!otherFaction.getId().equals(myFaction.getId())) { // If the faction target is not my own
            // Get faction pain build access relation to me
            boolean pain = !justCheck && otherFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW;
            return CheckPlayerAccess(player, me, loc, otherFaction, otherFaction.getAccess(me, action), action, pain);
        } else if (otherFaction.getId().equals(myFaction.getId())) {
            return CheckPlayerAccess(player, me, loc, myFaction, myFaction.getAccess(me, action), action,
                    (!justCheck && myFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW));
        }
        return CheckPlayerAccess(player, me, loc, myFaction, otherFaction.getAccess(me, action), action,
                Conf.territoryPainBuild);
    }

    public static boolean preventCommand(String fullCmd, Player player) {
        if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty()
                && Conf.permanentFactionMemberDenyCommands.isEmpty() && Conf.warzoneDenyCommands.isEmpty())) {
            return false;
        }

        fullCmd = fullCmd.toLowerCase();

        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        String shortCmd; // command without the slash at the beginning
        if (fullCmd.startsWith("/")) {
            shortCmd = fullCmd.substring(1);
        } else {
            shortCmd = fullCmd;
            fullCmd = "/" + fullCmd;
        }

        if (me.hasFaction() && !me.isAdminBypassing() && !Conf.permanentFactionMemberDenyCommands.isEmpty()
                && me.getFaction().isPermanent()
                && isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_PERMANENT, fullCmd);
            return true;
        }

        Faction at = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (at.isWilderness() && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing()
                && isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
            return true;
        }

        Relation rel = at.getRelationTo(me);
        if (at.isNormal() && rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing()
                && isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
            return false;
        }

        if (at.isNormal() && rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing()
                && isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_NEUTRAL, fullCmd);
            return true;
        }

        if (at.isNormal() && rel.isEnemy() && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing()
                && isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_ENEMY, fullCmd);
            return true;
        }

        if (at.isWarZone() && !Conf.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing()
                && isCommandInList(fullCmd, shortCmd, Conf.warzoneDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_WARZONE, fullCmd);
            return true;
        }

        return false;
    }

    private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
        String cmdCheck;
        while (iter.hasNext()) {
            cmdCheck = iter.next();
            if (cmdCheck == null) {
                iter.remove();
                continue;
            }

            cmdCheck = cmdCheck.toLowerCase();
            if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
                return true;
            }
        }
        return false;
    }

    private static boolean CheckPlayerAccess(Player player, FPlayer me, FLocation loc, Faction factionToCheck,
            Access access, PermissableAction action, boolean pain) {
        boolean doPain = pain || Conf.handleExploitInteractionSpam; // Painbuild should take priority. But we want to
                                                                    // use exploit interaction as well.
        if (access != null) {
            boolean landOwned = (factionToCheck.doesLocationHaveOwnersSet(loc) && !factionToCheck.getOwnerList(loc).isEmpty());
            if ((landOwned && factionToCheck.getOwnerListString(loc).contains(player.getName()) && me.getFaction().getOwnerclaimAccess(me, action) == Access.ALLOW)
                    || (me.getRole() == Role.LEADER && me.getFactionId().equals(factionToCheck.getId()))) {
                return true;
            } else if (landOwned && !factionToCheck.getOwnerListString(loc).contains(player.getName())) {
                me.msg(TL.ACTIONS_OWNEDTERRITORYDENY, factionToCheck.getOwnerListString(loc));
                if (doPain)
                    player.damage(Conf.actionDeniedPainAmount);
                return false;
            } else if (!landOwned && access == Access.ALLOW) {
                return true;
            } else {
                me.msg(TL.PLAYER_USE_TERRITORY, action, factionToCheck.getTag(me.getFaction()));
                return false;
            }
        }

        // Approves any permission check if the player in question is a leader AND owns
        // the faction.
        if (me.getRole().equals(Role.LEADER) && me.getFaction().equals(factionToCheck))
            return true;
        if (factionToCheck != null) {
            me.msg(TL.PLAYER_USE_TERRITORY, action, factionToCheck.getTag(me.getFaction()));
        }
        return false;
    }

    private static PermissableAction GetPermissionFromUsableBlock(Block block) {
        return GetPermissionFromUsableBlock(block.getType());
    }

    private static PermissableAction GetPermissionFromUsableBlock(Material material) {
        if (material.name().contains("_BUTTON")
                || material.name().contains("COMPARATOR")
                || material.name().contains("PRESSURE")
                || material.name().contains("REPEATER")
                || material.name().contains("DIODE")) return PermissableAction.BUTTON;
        if (material.name().contains("_DOOR")
                || material.name().contains("_TRAPDOOR")
                || material.name().contains("_FENCE_GATE")
                || material.name().startsWith("FENCE_GATE")) return PermissableAction.DOOR;
        if (material.name().contains("SHULKER_BOX")
                || material.name().endsWith("ANVIL")
                || material.name().startsWith("CHEST_MINECART")
                || material.name().endsWith("CHEST")
                || material.name().endsWith("JUKEBOX")
                || material.name().endsWith("CAULDRON")
                || material.name().endsWith("FURNACE")
                || material.name().endsWith("HOPPER")
                || material.name().endsWith("BEACON")
                || material.name().startsWith("TRAPPED_CHEST")
                || material.name().equalsIgnoreCase("ENCHANTING_TABLE")
                || material.name().equalsIgnoreCase("ENCHANTMENT_TABLE")
                || material.name().endsWith("BREWING_STAND")
                || material.name().equalsIgnoreCase("BARREL")) return PermissableAction.CONTAINER;
        if (material.name().endsWith("LEVER")) return PermissableAction.LEVER;
        switch (material) {
            case DISPENSER:
            case DROPPER:
                return PermissableAction.CONTAINER;
            default:
                return null;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        initPlayer(event.getPlayer());
    }

    private void initPlayer(Player player) {
        // Make sure that all online players do have a fplayer.
        final FPlayer me = FPlayers.getInstance().getByPlayer(player);
        ((MemoryFPlayer) me).setName(player.getName());

        // Update the lastLoginTime for this fplayer
        me.setLastLoginTime(System.currentTimeMillis());

        lastLocations.put(player.getUniqueId(), player.getLocation());

        // Store player's current FLocation and notify them where they are
        me.setLastStoodAt(new FLocation(player.getLocation()));

        me.login(); // set kills / deaths

        // Check for Faction announcements. Let's delay this so they actually see it.
        Bukkit.getScheduler().runTaskLater(SavageFactions.plugin, () -> {
            if (me.isOnline())
                me.getFaction().sendUnreadAnnouncements(me);
        }, 33L); // Don't ask me why.

        if (SavageFactions.plugin.getConfig().getBoolean("scoreboard.default-enabled", false)) {
            FScoreboard.init(me);
            FScoreboard.get(me).setDefaultSidebar(new FDefaultSidebar(),
                    SavageFactions.plugin.getConfig().getInt("scoreboard.default-update-interval", 20));
            FScoreboard.get(me).setSidebarVisibility(me.showScoreboard());
        }

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness()) {
            for (FPlayer other : myFaction.getFPlayersWhereOnline(true)) {
                if (other != me && other.isMonitoringJoins())
                    other.msg(TL.FACTION_LOGIN, me.getName());
            }
        }

        fallMap.put(me.getPlayer(), false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(SavageFactions.plugin, () -> fallMap.remove(me.getPlayer()),
                180L);

        if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
            me.setSpyingChat(false);
            SavageFactions.plugin.log(Level.INFO,
                    "Found %s spying chat without permission on login. Disabled their chat spying.", player.getName());
        }

        if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
            me.setIsAdminBypassing(false);
            SavageFactions.plugin.log(Level.INFO,
                    "Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
        }

        Bukkit.getScheduler().runTaskLater(SavageFactions.plugin, () -> {
            // Fix fly desync when joining
            if (me.isFlying() && !player.isFlying()) {
                me.setFFlying(false, false, false);
            }
        }, 5L);

        // If they have the permission, don't let them autoleave. Bad inverted setter :\
        me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
        me.setTakeFallDamage(true);
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) e.getEntity();
                if (fallMap.containsKey(player)) {
                    e.setCancelled(true);
                    fallMap.remove(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player this_ = event.getPlayer();
        FPlayer me = FPlayers.getInstance().getByPlayer(this_);

        // Make sure player's power is up to date when they log off.
        me.getPower();
        // and update their last login time to point to when the logged off, for
        // auto-remove routine
        me.setLastLoginTime(System.currentTimeMillis());

        me.logout(); // cache kills / deaths

        // if player is waiting for fstuck teleport but leaves, remove
        if (SavageFactions.plugin.getStuckMap().containsKey(me.getPlayer().getUniqueId())) {
            FPlayers.getInstance().getByPlayer(me.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED);
            SavageFactions.plugin.getStuckMap().remove(me.getPlayer().getUniqueId());
            SavageFactions.plugin.getTimers().remove(me.getPlayer().getUniqueId());
        }

        lastLocations.remove(this_.getUniqueId());

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness())
            myFaction.memberLoggedOff();

        if (!myFaction.isWilderness()) {
            for (FPlayer player : myFaction.getFPlayersWhereOnline(true))
                if (player != me && player.isMonitoringJoins())
                    player.msg(TL.FACTION_LOGOUT, me.getName());

        }

        CmdSeeChunk.seeChunkMap.remove(event.getPlayer().getName());

        FScoreboard.remove(me);
    }

    public String parseAllPlaceholders(String string, Faction faction, Player player) {
        string = TagUtil.parsePlaceholders(player, string);

        string = string.replace("{Faction}", faction.getTag())
                .replace("{online}", faction.getOnlinePlayers().size() + "")
                .replace("{offline}", faction.getFPlayers().size() - faction.getOnlinePlayers().size() + "")
                .replace("{chunks}", faction.getAllClaims().size() + "").replace("{power}", faction.getPower() + "")
                .replace("{leader}", faction.getFPlayerAdmin() + "");

        return string;
    }

    public void checkCanFly(FPlayer me) {
        if (!me.canFlyAtLocation(me.getLastStoodAt(),false) || me.checkIfNearbyEnemies()) {
            if (me.isFlying()) {
                me.setFFlying(false, false);
                me.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(me.getLastStoodAt()).getTag());
            }
            return;
        }
        if (me.isFlying() || !SavageFactions.plugin.getConfig().getBoolean("ffly.AutoEnable"))
            return;
        me.setFFlying(true, false);
        CmdFly.flyMap.put(me.getName(), true);
        if (CmdFly.particleTask == null && Conf.enableFlyParticles)
            CmdFly.startParticles();
    }

    // inspect
    @EventHandler
    public void onInspect(PlayerInteractEvent e) {
        if (e.getAction().name().contains("BLOCK")) {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
            if (!fplayer.isInspectMode())
                return;
            e.setCancelled(true);
            if (!fplayer.isAdminBypassing()) {
                if (!fplayer.hasFaction()) {
                    fplayer.setInspectMode(false);
                    fplayer.msg(TL.COMMAND_INSPECT_DISABLED_NOFAC);
                    return;
                }
                if (fplayer.getFaction() != Board.getInstance()
                        .getFactionAt(new FLocation(e.getPlayer().getLocation()))) {
                    fplayer.msg(TL.COMMAND_INSPECT_NOTINCLAIM);
                    return;
                }
            } else
                fplayer.msg(TL.COMMAND_INSPECT_BYPASS);
            List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
            if (info.isEmpty()) {
                e.getPlayer().sendMessage(TL.COMMAND_INSPECT_NODATA.toString());
                return;
            }
            Player player = e.getPlayer();
            CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
            player.sendMessage(TL.COMMAND_INSPECT_HEADER.toString().replace("{x}", e.getClickedBlock().getX() + "")
                    .replace("{y}", e.getClickedBlock().getY() + "").replace("{z}", e.getClickedBlock().getZ() + ""));
            String rowFormat = TL.COMMAND_INSPECT_ROW.toString();
            for (int i = 0; i < info.size(); i++) {
                CoreProtectAPI.ParseResult row = coAPI.parseResult(info.get(i));
                player.sendMessage(rowFormat.replace("{time}", convertTime(row.getTime()))
                        .replace("{action}", row.getActionString()).replace("{player}", row.getPlayer())
                        .replace("{block-type}", row.getType().toString().toLowerCase()));
            }
        }
    }

    // For disabling enderpearl throws
    @EventHandler
    public void onPearl(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (fPlayer.isFlying() && Conf.noEnderpearlsInFly) { // heh
                fPlayer.msg(TL.COMMAND_FLY_NO_EPEARL);
                e.setCancelled(true);
            }
        }
    }

    private String convertTime(int time) {
        String result = String.valueOf(Math.round((System.currentTimeMillis() / 1000L - time) / 36.0D) / 100.0D);
        return (result.length() == 3 ? result + "0" : result) + "/hrs ago";
    }

    public static BukkitTask positionTask = null;
    public final static Map<UUID, Location> lastLocations = new HashMap<>();

    public void startPositionCheck() {
        positionTask = Bukkit.getScheduler().runTaskTimer(SavageFactions.plugin, () -> {
            if (lastLocations.isEmpty()) return;
            for (Entry<UUID, Location> check : lastLocations.entrySet()) {
                Player player = Bukkit.getPlayer(check.getKey());
                refreshPosition(player, check.getValue(), player.getLocation());
                lastLocations.put(player.getUniqueId(), player.getLocation());
            }
        }, 5L, 10L);
    }

    public void refreshPosition(Player player, Location oldLocation, Location newLocation) {
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        // clear visualization
        if (oldLocation.getBlockX() != newLocation.getBlockX()
                || oldLocation.getBlockY() != newLocation.getBlockY()
                || oldLocation.getBlockZ() != newLocation.getBlockZ()) {
            VisualizeUtil.clear(player);
            if (me.isWarmingUp()) {
                me.clearWarmup();
                me.msg(TL.WARMUPS_CANCELLED);
            }
        }

        // quick check to make sure player is moving between chunks; good performance boost
        if (oldLocation.getChunk() == newLocation.getChunk() && oldLocation.getWorld().getName().equalsIgnoreCase(newLocation.getWorld().getName()))
            return;

        // Did we change coord?
        FLocation from = me.getLastStoodAt();
        FLocation to = new FLocation(player.getLocation());
        
        if (from.equals(to)) return;

        // Yes we did change coord (:
        me.setLastStoodAt(to);

        // Did we change "host"(faction)?
        Faction factionFrom = Board.getInstance().getFactionAt(from);
        Faction factionTo = Board.getInstance().getFactionAt(to);

        boolean changedFaction = (factionFrom != factionTo);

        if (changedFaction) {
            Bukkit.getScheduler().runTask(SavageFactions.plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new FPlayerEnteredFactionEvent(factionTo, factionFrom, me)));
            if (SavageFactions.plugin.getConfig().getBoolean("Title.Show-Title")) {
                String title = SavageFactions.plugin.getConfig().getString("Title.Format.Title");
                title = title.replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
                title = parseAllPlaceholders(title, factionTo, player);
                String subTitle = SavageFactions.plugin.getConfig().getString("Title.Format.Subtitle").replace("{Description}", factionTo.getDescription()).replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
                subTitle = parseAllPlaceholders(subTitle, factionTo, player);
                final String finalTitle = title;
                final String finalsubTitle = subTitle;
                if (!SavageFactions.plugin.mc17) {
                    Bukkit.getScheduler().runTaskLater(SavageFactions.plugin, () -> {
                        if (!SavageFactions.plugin.mc18) {
                            me.getPlayer().sendTitle(SavageFactions.plugin.color(finalTitle), SavageFactions.plugin.color(finalsubTitle), SavageFactions.plugin.getConfig().getInt("Title.Options.FadeInTime"),
                                    SavageFactions.plugin.getConfig().getInt("Title.Options.ShowTime"),
                                    SavageFactions.plugin.getConfig().getInt("Title.Options.FadeOutTime"));
                        } else {
                            me.getPlayer().sendTitle(SavageFactions.plugin.color(finalTitle), SavageFactions.plugin.color(finalsubTitle));
                        }
                    }, 5);
                }
            }
            // Handle fly
            this.checkCanFly(me);

            if (me.getAutoClaimFor() != null) {
                me.attemptClaim(me.getAutoClaimFor(), newLocation, true);
                if (Conf.disableFlightOnFactionClaimChange) CmdFly.disableFlight(me);
            } else if (me.isAutoSafeClaimEnabled()) {
                if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
                    me.setIsAutoSafeClaimEnabled(false);
                } else {
                    if (!Board.getInstance().getFactionAt(to).isSafeZone()) {
                        Board.getInstance().setFactionAt(Factions.getInstance().getSafeZone(), to);
                        me.msg(TL.PLAYER_SAFEAUTO);
                    }
                }
            } else if (me.isAutoWarClaimEnabled()) {
                if (!Permission.MANAGE_WAR_ZONE.has(player)) {
                    me.setIsAutoWarClaimEnabled(false);
                } else {
                    if (!Board.getInstance().getFactionAt(to).isWarZone()) {
                        Board.getInstance().setFactionAt(Factions.getInstance().getWarZone(), to);
                        me.msg(TL.PLAYER_WARAUTO);
                    }
                }
            }
        }

        if (me.isMapAutoUpdating()) {
            if (showTimes.containsKey(player.getUniqueId()) && (showTimes.get(player.getUniqueId()) > System.currentTimeMillis())) {
                if (SavageFactions.plugin.getConfig().getBoolean("findfactionsexploit.log", false)) {
                    SavageFactions.plugin.log(Level.WARNING, "%s tried to show a faction map too soon and triggered exploit blocker.", player.getName());
                }
            } else {
                me.sendFancyMessage(Board.getInstance().getMap(me, to, player.getLocation().getYaw()));
                showTimes.put(player.getUniqueId(), System.currentTimeMillis() + SavageFactions.plugin.getConfig().getLong("findfactionsexploit.cooldown", 2000));
            }
        } else {
            Faction myFaction = me.getFaction();
            String ownersTo = myFaction.getOwnerListString(to);
            if (changedFaction) {
                if (Conf.sendFactionChangeMessage) me.sendFactionHereMessage(factionFrom);
                if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
                    me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
                }
            } else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && myFaction == factionTo && !myFaction.isWilderness()) {
                String ownersFrom = myFaction.getOwnerListString(from);
                if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
                    if (!ownersTo.isEmpty()) {
                        me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
                    } else if (!TL.GENERIC_PUBLICLAND.toString().isEmpty()) {
                        me.sendMessage(TL.GENERIC_PUBLICLAND.toString());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        FPlayer fme = FPlayers.getInstance().getById(e.getPlayer().getUniqueId().toString());
        if (fme.isInVault()) fme.setInVault(false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block == null) return;


        Material type;
        if (event.getItem() != null) {
            // Convert 1.8 Material Names -> 1.14
            try {
                type = XMaterial.matchXMaterial(event.getItem().getType().toString()).get().parseMaterial();
            } catch (NullPointerException npe) {
                type = null;
            } catch (NoSuchElementException nsee) {
                type = null;
            }
        } else {
            type = null;
        }

        // Creeper Egg Bypass.
        if (Conf.allowCreeperEggingChests && block.getType() == Material.CHEST && type == XMaterial.CREEPER_SPAWN_EGG.parseMaterial() && event.getPlayer().isSneaking()) {
            return;
        }


        // territoryBypasssProtectedMaterials totally bypass the protection system
        if (Conf.territoryBypasssProtectedMaterials.contains(block.getType())) return;
        // Do type null checks so if XMaterial has a parsing issue and fills null as a value it will not bypass.
        // territoryCancelAndAllowItemUseMaterial bypass the protection system but only if they're not clicking on territoryDenySwitchMaterials
        // if they're clicking on territoryDenySwitchMaterials, let the protection system handle the permissions
        if (type != null && !Conf.territoryDenySwitchMaterials.contains(block.getType())) {
            if (Conf.territoryCancelAndAllowItemUseMaterial.contains(type)) {
                return;
            }
        }

        if (GetPermissionFromUsableBlock(block.getType()) != null) {
            if (!canPlayerUseBlock(player, block, false)) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                return;
            }
        }

        if (type != null && !playerCanUseItemHere(player, block.getLocation(), event.getItem().getType(), false, PermissableAction.ITEM)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onPlayerBoneMeal(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                && event.hasItem() && event.getItem().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), block.getLocation(), PermissableAction.BUILD.name(), true)) {
                FPlayer me = FPlayers.getInstance().getById(event.getPlayer().getUniqueId().toString());
                Faction myFaction = me.getFaction();

                me.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", myFaction.getTag(me.getFaction())).replace("{action}", "use bone meal"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

        me.getPower();  // update power, so they won't have gained any while dead

        Location home = me.getFaction().getHome();
        if (Conf.homesEnabled &&
                Conf.homesTeleportToOnDeath &&
                home != null &&
                (Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
            event.setRespawnLocation(home);
        }
    }

    // For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
    // but these separate bucket events below always fire without fail
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false, PermissableAction.BUILD)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false, PermissableAction.DESTROY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractGUI(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (event.getInventory().getHolder() instanceof FactionGUI) {
            event.setCancelled(true);
            ((FactionGUI) event.getInventory().getHolder()).onClick(event.getRawSlot(), event.getClick());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveGUI(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof FactionGUI) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        FPlayer badGuy = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (badGuy == null) return;

        // if player was banned (not just kicked), get rid of their stored info
        if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
            if (badGuy.getRole() == Role.LEADER) badGuy.getFaction().promoteNewLeader();

            badGuy.leave(false);
            badGuy.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    final public void onFactionJoin(FPlayerJoinEvent event) {
        FTeamWrapper.applyUpdatesLater(event.getFaction());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionLeave(FPlayerLeaveEvent event) {
        FTeamWrapper.applyUpdatesLater(event.getFaction());
    }

    // private static class InteractAttemptSpam {
    //     private int attempts = 0;
    //     private long lastAttempt = System.currentTimeMillis();

    //     // returns the current attempt count
    //     public int increment() {
    //         long now = System.currentTimeMillis();
    //         if (now > lastAttempt + 2000) attempts = 1;
    //         else attempts++;

    //         lastAttempt = now;
    //         return attempts;
    //     }
    // }
}
